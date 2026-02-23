package com.eagleeye.common.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁工具类
 * 支持加锁、解锁、续期等操作
 */
public class RedisLockUtil {

    private final StringRedisTemplate stringRedisTemplate;

    /**
     * Lua脚本：保证解锁的原子性
     * 只有锁的持有者才能解锁
     */
    private static final String UNLOCK_SCRIPT =
            "if redis.call('get', KEYS[1]) == ARGV[1] then " +
            "    return redis.call('del', KEYS[1]) " +
            "else " +
            "    return 0 " +
            "end";

    public RedisLockUtil(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 尝试获取锁
     *
     * @param lockKey   锁的key
     * @param requestId 请求唯一标识（用于解锁时验证）
     * @param expireTime 过期时间（秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime) {
        Boolean result = stringRedisTemplate.opsForValue()
                .setIfAbsent(lockKey, requestId, expireTime, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(result);
    }

    /**
     * 尝试获取锁（带等待时间）
     *
     * @param lockKey    锁的key
     * @param requestId  请求唯一标识
     * @param expireTime 过期时间（秒）
     * @param waitTime   等待时间（毫秒）
     * @return 是否获取成功
     */
    public boolean tryLock(String lockKey, String requestId, long expireTime, long waitTime) {
        long startTime = System.currentTimeMillis();
        long endTime = startTime + waitTime;

        while (System.currentTimeMillis() < endTime) {
            if (tryLock(lockKey, requestId, expireTime)) {
                return true;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    /**
     * 释放锁
     *
     * @param lockKey   锁的key
     * @param requestId 请求唯一标识
     * @return 是否释放成功
     */
    public boolean unlock(String lockKey, String requestId) {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>(UNLOCK_SCRIPT, Long.class);
        Long result = stringRedisTemplate.execute(redisScript, Collections.singletonList(lockKey), requestId);
        return result != null && result == 1;
    }

    /**
     * 续期锁（用于长时间业务）
     *
     * @param lockKey   锁的key
     * @param requestId 请求唯一标识
     * @param expireTime 过期时间（秒）
     * @return 是否续期成功
     */
    public boolean renewLock(String lockKey, String requestId, long expireTime) {
        // 只有锁的持有者才能续期
        String currentValue = stringRedisTemplate.opsForValue().get(lockKey);
        if (requestId.equals(currentValue)) {
            stringRedisTemplate.expire(lockKey, expireTime, TimeUnit.SECONDS);
            return true;
        }
        return false;
    }
}
