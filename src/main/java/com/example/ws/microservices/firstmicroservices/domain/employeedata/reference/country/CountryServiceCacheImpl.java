package com.example.ws.microservices.firstmicroservices.domain.employeedata.reference.country;

import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class CountryServiceCacheImpl implements CountryServiceCache {

    private final CountryRepositoryCache countryRepositoryCache;

    @Override
    public List<Country> findAll() {
        return countryRepositoryCache.findAll();
    }

    @Override
    public void preloadToCache(RedisTemplate<String, Object> redisTemplate) {
        List<CountryDTO> countries = getAllFromDB();
        redisTemplate.opsForValue().set("countries", countries);
//        for (CountryDTO country : countries) {
//            redisTemplate.opsForValue().set("country:" + country.getId(), country);
//        }
    }

    @Override
    public List<CountryDTO> getAllFromDB() {
        return countryRepositoryCache.findAllWithSite().stream().map(eachObject -> CountryDTO.builder()
                .name(eachObject.getName())
                .id(eachObject.getId())
                .siteName(eachObject.getSite().getName())
                .build()).toList();
    }

    @Override
    public boolean supportsType(Class<?> type) {
        return type.equals(CountryDTO.class);
    }
}
