package com.ef.firebasenotification.web;

import com.ef.firebasenotification.domain.DeviceToken;
import com.ef.firebasenotification.repository.DeviceTokenDAO;
import com.ef.firebasenotification.service.DeviceTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/device-token")
public class DeviceTokenREST {

    @Autowired DeviceTokenService service;

    @Autowired DeviceTokenDAO dao;

    @GetMapping("/")
    public ResponseEntity<List<DeviceToken>> getAll() throws Exception {
        List<DeviceToken> deviceTokens = service.getAll();
        return new ResponseEntity<List<DeviceToken>>(deviceTokens, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<DeviceToken> save(@RequestBody DeviceToken deviceToken) throws Exception {
        DeviceToken saveDeviceToken = service.save(deviceToken);
        return new ResponseEntity<DeviceToken>(saveDeviceToken, HttpStatus.OK);
    }

    @GetMapping("/getTokenByUserId")
    public ResponseEntity<List<DeviceToken>> getByUserId(@RequestParam("userId") Long userId) throws Exception {
        List<DeviceToken> deviceTokens = service.getByUserId(userId);
        return new ResponseEntity<List<DeviceToken>>(deviceTokens, HttpStatus.OK);
    }

    @GetMapping("/getTokenByUserIds")
    public ResponseEntity<List<DeviceToken>> getAllByUserIds(@RequestParam("userIds") List<Long> userIds) throws Exception {
        List<DeviceToken> deviceTokens = service.getAllByUserIds(userIds);
        return new ResponseEntity<List<DeviceToken>>(deviceTokens, HttpStatus.OK);
    }

    @PutMapping("/updateByToken")
    public ResponseEntity<CustomResponse> updateByToken(@RequestParam("oldToken") String oldToken, @RequestParam("newToken") String newToken) throws Exception {
        Integer updateRowCount = service.updateByToken(oldToken, newToken);
        if (updateRowCount != 0) {
            return new ResponseEntity<>(new CustomResponse(HttpStatus.OK.toString()), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new CustomResponse(HttpStatus.NOT_MODIFIED.toString()), HttpStatus.NOT_MODIFIED);
        }
    }

    @DeleteMapping("/deleteByToken")
    public ResponseEntity<CustomResponse> deleteByToken(@RequestParam("token") String token) throws Exception {
        DeviceToken deviceToken = service.findByToken(token);
        if (deviceToken == null) {
            return new ResponseEntity<>(new CustomResponse(HttpStatus.NOT_FOUND.toString()), HttpStatus.NOT_FOUND);
        } else {
            service.deleteByToken(deviceToken.getId());
            return new ResponseEntity<>(new CustomResponse(HttpStatus.OK.toString()), HttpStatus.OK);
        }
    }

}
