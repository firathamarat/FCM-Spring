package com.ef.firebasenotification.repository;

import com.ef.firebasenotification.domain.DeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceTokenDAO extends JpaRepository<DeviceToken, Long> {

    @Query(nativeQuery = true, value = "SELECT * FROM device_token")
    List<DeviceToken> getAll();

    @Query(nativeQuery = true, value = "SELECT * FROM device_token dt WHERE dt.user_id = ?1")
    List<DeviceToken> getByUserId(Long userId);

    @Query(nativeQuery = true, value = "SELECT * FROM device_token dt WHERE dt.user_id IN (?1)")
    List<DeviceToken> getAllByUserIds(List<Long> userIds);

    @Query(nativeQuery = true, value = "SELECT * FROM device_token dt WHERE dt.device_token = ?1")
    DeviceToken findByToken(String token);

    @Query(nativeQuery = true, value = "SELECT * FROM device_token dt WHERE dt.user_id = ?1")
    DeviceToken findTokenByUserId(Long userId);

    @Modifying
    @Query(nativeQuery = true, value = "UPDATE device_token SET device_token = ?2 WHERE device_token = ?1")
    Integer updateByToken(String oldToken, String newToken);

    @Modifying
    @Query(nativeQuery = true, value = "DELETE FROM device_token dt WHERE dt.id = ?1")
    void deleteByToken(Long id);

}
