package com.blog.service;

import com.blog.entity.SystemSetting;
import com.blog.repository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SystemSettingService {

    @Autowired
    private SystemSettingRepository settingRepository;

    public Map<String, String> getAllSettings() {
        List<SystemSetting> settings = settingRepository.findAll();
        return settings.stream()
                .collect(Collectors.toMap(SystemSetting::getSettingKey, SystemSetting::getSettingValue));
    }

    public String getSetting(String key) {
        return settingRepository.findBySettingKey(key)
                .map(SystemSetting::getSettingValue)
                .orElse(null);
    }

    @Transactional
    public void updateSetting(String key, String value) {
        SystemSetting setting = settingRepository.findBySettingKey(key)
                .orElse(new SystemSetting());
        setting.setSettingKey(key);
        setting.setSettingValue(value);
        settingRepository.save(setting);
    }

    @Transactional
    public void updateSettings(Map<String, String> settings) {
        settings.forEach(this::updateSetting);
    }

    @Transactional
    public void initDefaultSettings() {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("site_name", "我的博客");
        defaults.put("site_description", "一个分享知识的个人博客");
        defaults.put("site_logo", "/images/logo.png");
        defaults.put("icp_number", "");
        defaults.put("police_number", "");
        defaults.put("police_link", "");
        defaults.put("comment_enabled", "true");
        defaults.put("registration_enabled", "true");
        defaults.put("default_role", "USER");

        defaults.forEach((key, value) -> {
            if (settingRepository.findBySettingKey(key).isEmpty()) {
                SystemSetting setting = new SystemSetting();
                setting.setSettingKey(key);
                setting.setSettingValue(value);
                setting.setSettingType(value.equals("true") || value.equals("false") ? "BOOLEAN" : "STRING");
                settingRepository.save(setting);
            }
        });
    }
}
