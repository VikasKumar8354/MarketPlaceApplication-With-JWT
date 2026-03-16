package com.MarketPlace.emailService;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {

    private record OtpData(String otp, Instant expiry) {}

    private final Map<String, OtpData> otpCache = new ConcurrentHashMap<>();
    private final Random random = new Random();

    // Generate OTP for email or phone
    public String generateOtp(String key) {
        String otp = String.format("%06d", random.nextInt(1_000_000)); // 6-digit OTP
        Instant expiry = Instant.now().plusSeconds(5 * 60); // 5 min
        otpCache.put(key, new OtpData(otp, expiry));
        // TODO: Send OTP via SMS or Email API
        return otp;
    }

    // Verify OTP
    public boolean verifyOtp(String key, String otp) {
        OtpData data = otpCache.get(key);
        if (data == null) return false;

        if (Instant.now().isAfter(data.expiry)) {
            otpCache.remove(key);
            return false;
        }

        boolean valid = data.otp.equals(otp);
        if (valid) otpCache.remove(key);
        return valid;
    }
}
