package com.eagleeye.system.oom;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OOM（内存溢出）演示类
 * 展示各种常见的OOM场景
 */
@Slf4j
public class OOMDemo {

    /**
     * 场景2: 内存泄漏导致的OOM
     * 使用静态集合持有对象引用，导致无法GC
     */
    private static final List<Object> STATIC_LIST = new ArrayList<>();

    /**
     * 场景1: Java堆内存溢出
     * 不断创建对象，直到堆内存耗尽
     */
    @Test
    public void testHeapOOM() {
        log.info("========== 堆内存OOM测试开始 ==========");
        log.info("JVM最大堆内存: {}MB", Runtime.getRuntime().maxMemory() / 1024 / 1024);

        // 使用固定容量，避免扩容开销
        List<byte[]> list = new ArrayList<>(10000);
        int count = 0;

        try {
            while (true) {
                // 每次分配1MB的数组
                byte[] bytes = new byte[1024 * 1024];
                log.info("byte{}", bytes[1]);
                list.add(bytes);
                count++;

                if (count % 100 == 0) {
                    Runtime runtime = Runtime.getRuntime();
                    long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
                    long maxMemory = runtime.maxMemory() / 1024 / 1024;
                    log.info("已创建{}个对象, 已用内存={}MB, 最大内存={}MB, 理论数据大小={}MB",
                            count, usedMemory, maxMemory, count);
                }
            }
        } catch (OutOfMemoryError e) {
            log.error("========== 堆内存OOM发生 ==========");
            log.error("在创建{}个对象后发生OOM", count);
            log.error("错误信息: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 场景1b: 对比 - 不持有引用，观察GC效果
     */
    @Test
    public void testHeapOOMWithGC() {
        log.info("========== 带GC的堆内存测试开始 ==========");
        log.info("此测试不持有对象引用，观察GC是否能回收内存");
        log.info("JVM最大堆内存: {}MB", Runtime.getRuntime().maxMemory() / 1024 / 1024);

        // 注意：这里不使用list持有引用！
        int count = 0;

        try {
            while (true) {
                // 创建1MB数组，但不持有引用（局部变量，方法结束即可回收）
                byte[] bytes = new byte[1024 * 1024];
                // 使用一下数组，防止被JIT优化掉
                bytes[0] = (byte) (count % 128);
                // 不添加到任何集合，让bytes成为垃圾对象
                count++;

                if (count % 100 == 0) {
                    // 强制GC
                    System.gc();

                    Runtime runtime = Runtime.getRuntime();
                    long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
                    long maxMemory = runtime.maxMemory() / 1024 / 1024;
                    log.info("已创建{}个对象, 已用内存={}MB, 最大内存={}MB", count, usedMemory, maxMemory);
                }
            }
        } catch (OutOfMemoryError e) {
            log.error("========== OOM发生 ==========");
            log.error("在创建{}个对象后发生OOM", count);
            log.error("错误信息: {}", e.getMessage());
            throw e;
        }
    }

    @Test
    public void testMemoryLeakOOM() {
        log.info("========== 内存泄漏OOM测试开始 ==========");
        log.info("使用静态集合持有对象，导致无法被GC");

        int count = 0;

        try {
            while (true) {
                // 创建大对象并放入静态集合
                STATIC_LIST.add(new byte[1024 * 1024]);  // 1MB
                count++;

                if (count % 100 == 0) {
                    Runtime runtime = Runtime.getRuntime();
                    long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
                    log.info("静态列表大小={}, 已用内存={}MB", STATIC_LIST.size(), usedMemory);
                }
            }
        } catch (OutOfMemoryError e) {
            log.error("========== 内存泄漏OOM发生 ==========");
            log.error("静态列表最终大小: {}", STATIC_LIST.size());
            log.error("错误信息: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 场景3: 错误的hashCode导致的OOM
     * hashCode()返回随机值，导致HashMap无法正确去重
     */
    @Test
    public void testBadHashCodeOOM() {
        log.info("========== 错误hashCode导致的OOM测试开始 ==========");
        log.info("BadKey的hashCode()返回随机值，导致HashMap认为每个key都是新的");

        Map<BadKey, String> map = new HashMap<>();
        int count = 0;

        try {
            while (true) {
                // 使用相同的逻辑值，但hashCode随机
                map.put(new BadKey(1), "value" + count);
                count++;

                if (count % 10000 == 0) {
                    Runtime runtime = Runtime.getRuntime();
                    long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
                    log.info("已添加{}个元素, Map大小={}, 已用内存={}MB", count, map.size(), usedMemory);
                }

                // Map大小应该为1，但由于hashCode随机，会不断增长
                if (map.size() > 100000) {
                    log.warn("警告: Map大小({})远超预期(1)，存在严重内存泄漏！", map.size());
                }
            }
        } catch (OutOfMemoryError e) {
            log.error("========== OOM发生 ==========");
            log.error("在添加{}个元素后发生OOM", count);
            log.error("Map最终大小: {} (预期只有1)", map.size());
            log.error("错误信息: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 场景4: 字符串常量池溢出（JDK7+已移至堆，此测试在JDK6有效）
     * 不断调用intern()方法
     */
    @Test
    public void testStringInternOOM() {
        log.info("========== 字符串常量池OOM测试开始 ==========");

        List<String> list = new ArrayList<>();
        int count = 0;

        try {
            while (true) {
                // 创建不同的字符串并intern
                String str = "String_" + count + "_" + System.currentTimeMillis();
                list.add(str.intern());
                count++;

                if (count % 100000 == 0) {
                    Runtime runtime = Runtime.getRuntime();
                    long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
                    log.info("已intern{}个字符串, 已用内存={}MB", count, usedMemory);
                }
            }
        } catch (OutOfMemoryError e) {
            log.error("========== 字符串常量池OOM发生 ==========");
            log.error("在intern{}个字符串后发生OOM", count);
            log.error("错误信息: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 场景5: 创建大量线程导致的OOM
     * 每个线程需要分配栈空间
     */
    @Test
    public void testUnableCreateNewThreadOOM() {
        log.info("========== 无法创建新线程OOM测试开始 ==========");

        List<Thread> threads = new ArrayList<>();
        int count = 0;

        try {
            while (true) {
                Thread thread = new Thread(() -> {
                    try {
                        Thread.sleep(100000);  // 长时间休眠
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });
                thread.start();
                threads.add(thread);
                count++;

                if (count % 100 == 0) {
                    log.info("已创建{}个线程", count);
                }
            }
        } catch (OutOfMemoryError e) {
            log.error("========== 无法创建新线程OOM发生 ==========");
            log.error("在创建{}个线程后发生OOM", count);
            log.error("错误信息: {}", e.getMessage());

            // 清理线程
            for (Thread t : threads) {
                t.interrupt();
            }
            throw e;
        }
    }

    /**
     * 错误的Key实现 - hashCode返回随机值
     */
    static class BadKey {
        private final int value;
        private final int randomHash;

        public BadKey(int value) {
            this.value = value;
            this.randomHash = (int) (Math.random() * Integer.MAX_VALUE);
        }

        @Override
        public int hashCode() {
            return randomHash;  // 每次返回不同的hashCode!
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            BadKey badKey = (BadKey) obj;
            return value == badKey.value;
        }
    }
}
