package com.ef.firebasenotification.service;

import com.ef.firebasenotification.domain.DeviceToken;
import com.ef.firebasenotification.repository.DeviceTokenDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DeviceTokenService {

    @Autowired DeviceTokenDAO dao;

    public List<DeviceToken> getAll() {
        return dao.getAll();
    }
    public DeviceToken save(DeviceToken entity) throws Exception {
        DeviceToken deviceToken = findByToken(entity.getDeviceToken());
        if (deviceToken != null) {
            deviceToken.setUpdateDate(LocalDateTime.now());
            return dao.save(deviceToken);
        } else {
            entity.setCreateDate(LocalDateTime.now());
            entity.setUpdateDate(LocalDateTime.now());
            return dao.save(entity);
        }
    }

    public List<DeviceToken> getByUserId(Long userId) {
        return dao.getByUserId(userId);
    }

    public List<DeviceToken> getAllByUserIds(List<Long> userIds) {
        return dao.getAllByUserIds(userIds);
    }

    public DeviceToken findByToken(String token) {
        return dao.findByToken(token);
    }

    public DeviceToken findTokenByUserId(Long userId) {
        return dao.findTokenByUserId(userId);
    }

    public Integer updateByToken(String oldToken, String newToken) {
        return dao.updateByToken(oldToken, newToken);
    }

    public void deleteByToken(Long id) {
        dao.deleteByToken(id);
    }

}
