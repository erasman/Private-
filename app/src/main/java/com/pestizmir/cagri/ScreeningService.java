package com.pestizmir.cagri;

import android.net.Uri;
import android.telecom.Call;
import android.telecom.CallScreeningService;

public class ScreeningService extends CallScreeningService {

    @Override
    public void onScreenCall(Call.Details callDetails) {
        // Aramaya hiçbir müdahale etmiyoruz: olduğu gibi geçsin.
        CallResponse response = new CallResponse.Builder()
                .setDisallowCall(false)
                .setRejectCall(false)
                .setSkipCallLog(false)
                .setSkipNotification(false)
                .build();
        respondToCall(callDetails, response);

        // Sadece GELEN aramalarda kartı göster.
        if (callDetails.getCallDirection() == Call.Details.DIRECTION_INCOMING) {
            String number = "";
            Uri handle = callDetails.getHandle();
            if (handle != null) {
                number = handle.getSchemeSpecificPart();
            }
            OverlayController.show(getApplicationContext(), number);
        }
    }
}
