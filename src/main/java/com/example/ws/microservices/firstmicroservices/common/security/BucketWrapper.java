package com.example.ws.microservices.firstmicroservices.common.security;

import io.github.bucket4j.Bucket;
import lombok.Getter;

/**
 * A wrapper for Bucket4j's Bucket that tracks the last time the bucket was accessed.
 * This enables efficient cleanup of unused or inactive buckets.
 * <p>
 * Responsibilities:
 * 1. Wrap a Bucket instance for rate-limiting.
 * 2. Maintain a timestamp of the last access to the bucket.
 * 3. Provide methods to update and retrieve the lastAccessed timestamp.
 * <p>
 * Thread-Safety:
 * This class is thread-safe due to the use of `volatile` for the lastAccessed field,
 * ensuring consistent reads and writes in a concurrent environment.
 */
@Getter
public class BucketWrapper {


    private final Bucket bucket;
    private volatile long lastAccessed;

    /**
     * Constructs a new BucketWrapper and sets the initial lastAccessed timestamp.
     *
     * @param bucket the Bucket instance to wrap.
     */
    public BucketWrapper(Bucket bucket) {
        this.bucket = bucket;
        this.lastAccessed = System.currentTimeMillis();
    }

    /**
     * Updates the lastAccessed timestamp to the current time.
     */
    public void updateLastAccessed() {
        this.lastAccessed = System.currentTimeMillis();
    }

}
