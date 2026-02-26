package com.eagleeye.system.concurrent;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ArrayList并发测试
 * 演示在不加锁的情况下，多线程操作ArrayList的线程安全问题
 */
@Slf4j
public class ArrayListConcurrencyTest {

    // 线程数
    private static final int THREAD_COUNT = 10;
    // 每个线程添加的元素数
    private static final int ELEMENTS_PER_THREAD = 100;
    // 重复执行次数
    private static final int REPEAT_COUNT = 50;

    /**
     * 测试1: 普通ArrayList并发添加 - 演示元素丢失问题
     */
    @Test
    public void testArrayListConcurrency() throws InterruptedException {
        log.info("========== ArrayList并发测试开始 ==========");
        log.info("线程数: {}, 每线程添加元素数: {}, 重复次数: {}",
                THREAD_COUNT, ELEMENTS_PER_THREAD, REPEAT_COUNT);

        int lossCount = 0;  // 元素丢失次数
        int exceptionCount = 0;  // 异常次数

        for (int round = 1; round <= REPEAT_COUNT; round++) {
            List<String> arrayList = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            AtomicInteger errorCount = new AtomicInteger(0);

            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < ELEMENTS_PER_THREAD; j++) {
                            String element = "Thread" + threadId + "_Element" + j;
                            arrayList.add(element);  // 直接添加，不检查
                        }
                    } catch (Exception e) {
                        log.error("线程{}发生异常: {}", threadId, e.getClass().getSimpleName());
                        errorCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            int expectedSize = THREAD_COUNT * ELEMENTS_PER_THREAD;
            int actualSize = arrayList.size();
            boolean hasLoss = actualSize < expectedSize;  // 元素丢失
            boolean hasException = errorCount.get() > 0;

            if (hasLoss) {
                lossCount++;
            }
            if (hasException) {
                exceptionCount++;
            }

            log.info("第{}轮: 期望={}, 实际={}, 丢失={}, 异常={}",
                    round, expectedSize, actualSize,
                    hasLoss ? "是(-" + (expectedSize - actualSize) + ")" : "否",
                    hasException ? "是" : "否");
        }

        log.info("========== 测试结果统计 ==========");
        log.info("总测试次数: {}", REPEAT_COUNT);
        log.info("元素丢失次数: {} ({}%)", lossCount, (lossCount * 100.0 / REPEAT_COUNT));
        log.info("出现异常次数: {} ({}%)", exceptionCount, (exceptionCount * 100.0 / REPEAT_COUNT));
    }

    /**
     * 测试2: 所有线程添加相同元素 - 演示重复添加问题
     */
    @Test
    public void testDuplicateAdd() throws InterruptedException {
        log.info("========== 重复添加测试开始 ==========");
        log.info("所有线程尝试添加相同的{}个元素", ELEMENTS_PER_THREAD);

        int duplicateCount = 0;

        for (int round = 1; round <= REPEAT_COUNT; round++) {
            // 使用普通ArrayList
            List<String> arrayList = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < ELEMENTS_PER_THREAD; j++) {
                            String element = "Shared_Element" + j;  // 所有线程添加相同元素

                            // 先检查再添加（经典的check-then-act竞争条件）
                            if (!arrayList.contains(element)) {
                                arrayList.add(element);
                            }
                        }
                    } catch (Exception e) {
                        log.error("线程{}异常: {}", threadId, e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            int expectedSize = ELEMENTS_PER_THREAD;  // 期望只有100个唯一元素
            int actualSize = arrayList.size();
            boolean hasDuplicate = actualSize > expectedSize;

            if (hasDuplicate) {
                duplicateCount++;
            }

            // 统计重复情况
            List<String> duplicates = findDuplicates(arrayList);

            log.info("第{}轮: 期望={}, 实际={}, 重复={}, 重复元素数={}",
                    round, expectedSize, actualSize,
                    hasDuplicate ? "是(+)" : "否",
                    duplicates.size());
        }

        log.info("========== 重复添加测试统计 ==========");
        log.info("总测试次数: {}", REPEAT_COUNT);
        log.info("出现重复的次数: {} ({}%)", duplicateCount, (duplicateCount * 100.0 / REPEAT_COUNT));
    }

    /**
     * 测试3: 使用synchronized块解决线程安全问题
     * 注意：既然已经用了synchronized块，用普通ArrayList也可以
     */
    @Test
    public void testSynchronizedBlock() throws InterruptedException {
        log.info("========== synchronized块测试开始 ==========");

        int duplicateCount = 0;

        for (int round = 1; round <= REPEAT_COUNT; round++) {
            // 用普通ArrayList，靠synchronized块保证线程安全
            List<String> list = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < ELEMENTS_PER_THREAD; j++) {
                            String element = "Shared_Element" + j;
                            // synchronized块保证check-then-act原子性
                            synchronized (list) {
                                if (!list.contains(element)) {
                                    try {
                                        Thread.sleep(1);
                                    } catch (InterruptedException e) {
                                        Thread.currentThread().interrupt();
                                    }
                                    list.add(element);
                                }
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            int expectedSize = ELEMENTS_PER_THREAD;
            int actualSize = list.size();
            boolean hasDuplicate = actualSize > expectedSize;

            if (hasDuplicate) {
                duplicateCount++;
            }

            log.info("第{}轮: 期望={}, 实际={}, 重复={}",
                    round, expectedSize, actualSize, hasDuplicate ? "是" : "否");
        }

        log.info("========== synchronized块测试结果 ==========");
        log.info("出现重复的次数: {} ({}%)", duplicateCount, (duplicateCount * 100.0 / REPEAT_COUNT));
    }

    /**
     * 测试4: 对比 - 只用synchronizedList，不加synchronized块
     * 预期：仍然会出现重复，因为contains和add是两个独立的方法
     */
    @Test
    public void testSynchronizedListOnly() throws InterruptedException {
        log.info("========== 仅synchronizedList测试（预期会失败） ==========");

        int duplicateCount = 0;

        for (int round = 1; round <= REPEAT_COUNT; round++) {
            List<String> syncList = Collections.synchronizedList(new ArrayList<>());
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < ELEMENTS_PER_THREAD; j++) {
                            String element = "Shared_Element" + j;
                            // 不加synchronized块，只靠synchronizedList的方法级同步
                            if (!syncList.contains(element)) {
                                syncList.add(element);
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            int expectedSize = ELEMENTS_PER_THREAD;
            int actualSize = syncList.size();
            boolean hasDuplicate = actualSize > expectedSize;

            if (hasDuplicate) {
                duplicateCount++;
            }

            log.info("第{}轮: 期望={}, 实际={}, 重复={}",
                    round, expectedSize, actualSize, hasDuplicate ? "是" : "否");
        }

        log.info("========== 仅synchronizedList测试结果 ==========");
        log.info("出现重复的次数: {} ({}%) - 证明方法级同步不够", duplicateCount, (duplicateCount * 100.0 / REPEAT_COUNT));
    }

    /**
     * 测试5: synchronizedList能避免元素丢失
     * 每个线程添加不同元素，验证synchronizedList能保证元素不丢失
     */
    @Test
    public void testSynchronizedListNoLoss() throws InterruptedException {
        log.info("========== synchronizedList元素不丢失测试 ==========");

        int lossCount = 0;
        int exceptionCount = 0;

        for (int round = 1; round <= REPEAT_COUNT; round++) {
            List<String> syncList = Collections.synchronizedList(new ArrayList<>());
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
            AtomicInteger errorCount = new AtomicInteger(0);

            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < ELEMENTS_PER_THREAD; j++) {
                            String element = "Thread" + threadId + "_Element" + j;
                            syncList.add(element);  // 直接添加，synchronizedList保证add线程安全
                        }
                    } catch (Exception e) {
                        log.error("线程{}异常: {}", threadId, e.getClass().getSimpleName());
                        errorCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            int expectedSize = THREAD_COUNT * ELEMENTS_PER_THREAD;
            int actualSize = syncList.size();
            boolean hasLoss = actualSize < expectedSize;
            boolean hasException = errorCount.get() > 0;

            if (hasLoss) {
                lossCount++;
            }
            if (hasException) {
                exceptionCount++;
            }

            log.info("第{}轮: 期望={}, 实际={}, 丢失={}, 异常={}",
                    round, expectedSize, actualSize,
                    hasLoss ? "是(-" + (expectedSize - actualSize) + ")" : "否",
                    hasException ? "是" : "否");
        }

        log.info("========== synchronizedList元素不丢失测试结果 ==========");
        log.info("元素丢失次数: {} ({}%)", lossCount, (lossCount * 100.0 / REPEAT_COUNT));
        log.info("出现异常次数: {} ({}%)", exceptionCount, (exceptionCount * 100.0 / REPEAT_COUNT));
        log.info("结论: synchronizedList能保证单个操作线程安全，避免元素丢失");
    }

    /**
     * 测试6: 使用ReentrantLock实现线程安全
     * 不用synchronized关键字，用Lock实现check-then-act原子性
     */
    @Test
    public void testReentrantLock() throws InterruptedException {
        log.info("========== ReentrantLock测试开始 ==========");

        int duplicateCount = 0;

        for (int round = 1; round <= REPEAT_COUNT; round++) {
            List<String> list = new ArrayList<>();
            Lock lock = new ReentrantLock();  // 创建锁对象
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < ELEMENTS_PER_THREAD; j++) {
                            String element = "Shared_Element" + j;

                            // 使用Lock实现同步
                            lock.lock();  // 获取锁
                            try {
                                if (!list.contains(element)) {
                                    list.add(element);
                                }
                            } finally {
                                lock.unlock();  // 释放锁（必须在finally中）
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            int expectedSize = ELEMENTS_PER_THREAD;
            int actualSize = list.size();
            boolean hasDuplicate = actualSize > expectedSize;

            if (hasDuplicate) {
                duplicateCount++;
            }

            log.info("第{}轮: 期望={}, 实际={}, 重复={}",
                    round, expectedSize, actualSize, hasDuplicate ? "是" : "否");
        }

        log.info("========== ReentrantLock测试结果 ==========");
        log.info("出现重复的次数: {} ({}%)", duplicateCount, (duplicateCount * 100.0 / REPEAT_COUNT));
        log.info("结论: ReentrantLock可以实现与synchronized相同的线程安全效果");
    }

    /**
     * 测试7: 使用CopyOnWriteArrayList解决并发问题
     * CopyOnWriteArrayList是线程安全的，适合读多写少的场景
     */
    @Test
    public void testCopyOnWriteArrayList() throws InterruptedException {
        log.info("========== CopyOnWriteArrayList测试开始 ==========");
        log.info("注意: CopyOnWriteArrayList的contains和add都是线程安全的，但复合操作仍需注意");

        int duplicateCount = 0;
        int lossCount = 0;

        for (int round = 1; round <= REPEAT_COUNT; round++) {
            // CopyOnWriteArrayList是线程安全的List实现
            List<String> cowList = new CopyOnWriteArrayList<>();
            CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
            ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);

            for (int i = 0; i < THREAD_COUNT; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < ELEMENTS_PER_THREAD; j++) {
                            String element = "Shared_Element" + j;
                            // CopyOnWriteArrayList的contains和add都是线程安全的
                            // 但contains和add是两个独立操作，中间可能有其他线程插入
                            // 所以仍然可能出现重复（虽然概率较低）
                            if (!cowList.contains(element)) {
                                cowList.add(element);
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
            executor.shutdown();

            int expectedSize = ELEMENTS_PER_THREAD;
            int actualSize = cowList.size();
            boolean hasDuplicate = actualSize > expectedSize;
            boolean hasLoss = actualSize < expectedSize;

            if (hasDuplicate) {
                duplicateCount++;
            }
            if (hasLoss) {
                lossCount++;
            }

            log.info("第{}轮: 期望={}, 实际={}, 重复={}, 丢失={}",
                    round, expectedSize, actualSize,
                    hasDuplicate ? "是" : "否",
                    hasLoss ? "是" : "否");
        }

        log.info("========== CopyOnWriteArrayList测试结果 ==========");
        log.info("出现重复的次数: {} ({}%)", duplicateCount, (duplicateCount * 100.0 / REPEAT_COUNT));
        log.info("元素丢失次数: {} ({}%)", lossCount, (lossCount * 100.0 / REPEAT_COUNT));
        log.info("结论: CopyOnWriteArrayList能保证单个操作线程安全，但复合操作仍可能重复");
        log.info("适用场景: 读多写少，且能接受最终一致性的场景");
    }

    /**
     * 查找列表中的重复元素
     */
    private List<String> findDuplicates(List<String> list) {
        List<String> duplicates = new ArrayList<>();
        List<String> seen = new ArrayList<>();

        for (String element : list) {
            if (seen.contains(element)) {
                duplicates.add(element);
            } else {
                seen.add(element);
            }
        }

        return duplicates;
    }
}
