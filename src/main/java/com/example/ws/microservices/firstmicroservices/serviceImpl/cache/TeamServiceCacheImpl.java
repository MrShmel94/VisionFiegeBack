package com.example.ws.microservices.firstmicroservices.serviceImpl.cache;

import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.dto.TeamDTO;
import com.example.ws.microservices.firstmicroservices.domain.employeedata.employeemapping.reference.Team;
import com.example.ws.microservices.firstmicroservices.repository.cache.TeamRepositoryCache;
import com.example.ws.microservices.firstmicroservices.service.cache.TeamServiceCache;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TeamServiceCacheImpl implements TeamServiceCache {

    private final TeamRepositoryCache teamRepositoryCache;

    @Override
    public List<Team> findAll() {
        return teamRepositoryCache.findAll();
    }

    @Override
    public void preloadToCache(RedisTemplate<String, Object> redisTemplate) {
        List<TeamDTO> teams = getAllFromDB();
        redisTemplate.opsForValue().set("teams", teams);
//        for (TeamDTO team : teams) {
//            redisTemplate.opsForValue().set("team:" + team.getId(), team);
//        }
    }

    @Override
    public List<TeamDTO> getAllFromDB() {
        return teamRepositoryCache.findAllWithSite().stream().map(eachObject -> TeamDTO.builder()
                .name(eachObject.getName())
                .id(eachObject.getId())
                .siteName(eachObject.getSite().getName())
                .build()).toList();
    }

    @Override
    public boolean supportsType(Class<?> type) {
        return type.equals(TeamDTO.class);
    }
}
