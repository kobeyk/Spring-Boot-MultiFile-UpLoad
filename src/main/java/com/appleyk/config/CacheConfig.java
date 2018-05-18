package com.appleyk.config;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.appleyk.file.FileUploadState;

@Configuration
public class CacheConfig {

	/*
	 * 文件上传缓存
	 */
    @Bean(name = "cacheFile")  
    public CacheManager cacheFile() {
        CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().
                withCache("fileState",
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(String.class, FileUploadState.class,
                                ResourcePoolsBuilder.heap(100)).build()).
                build(true);
        return cacheManager;
    }   
}
