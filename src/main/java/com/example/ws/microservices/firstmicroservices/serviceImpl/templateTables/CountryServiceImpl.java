package com.example.ws.microservices.firstmicroservices.serviceImpl.templateTables;

import com.example.ws.microservices.firstmicroservices.dto.templateTables.AgencyDTO;
import com.example.ws.microservices.firstmicroservices.dto.templateTables.CountryDTO;
import com.example.ws.microservices.firstmicroservices.entity.template.Agency;
import com.example.ws.microservices.firstmicroservices.entity.template.Country;
import com.example.ws.microservices.firstmicroservices.repository.templateTables.AgencyRepository;
import com.example.ws.microservices.firstmicroservices.repository.templateTables.CountryRepository;
import com.example.ws.microservices.firstmicroservices.service.templateTables.AgencyService;
import com.example.ws.microservices.firstmicroservices.service.templateTables.CountryService;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class CountryServiceImpl implements CountryService {

    private final CountryRepository countryRepository;

    @Override
    public List<Country> findAll() {
        return countryRepository.findAll();
    }

    @Override
    public void preloadToCache(RedisTemplate<String, Object> redisTemplate) {
        List<CountryDTO> countries = findAllWithSite();
        for (CountryDTO country : countries) {
            redisTemplate.opsForValue().set("country:" + country.getId(), country);
        }
    }

    @Override
    public List<CountryDTO> findAllWithSite() {
        return countryRepository.findAllWithSite().stream().map(eachObject -> CountryDTO.builder()
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
