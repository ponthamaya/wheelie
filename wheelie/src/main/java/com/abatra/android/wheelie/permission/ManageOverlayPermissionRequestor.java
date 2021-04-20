package com.abatra.android.wheelie.permission;

import androidx.activity.result.contract.ActivityResultContract;

import static com.abatra.android.wheelie.permission.SinglePermissionGrantResult.Builder;

public class ManageOverlayPermissionRequestor extends AbstractSinglePermissionRequestor {

    @Override
    protected ActivityResultContract<String, Boolean> createRequestPermissionActivityResultContract() {
        return new ManageOverlayPermissionContract(getLifecycleOwner().getContext());
    }

    @Override
    protected boolean isPermissionGranted(String permission) {
        return PermissionUtils.canDrawOverlays(getLifecycleOwner().getContext());
    }

    @Override
    protected Builder createBuilder() {
        return ManageOverlayPermissionGrantResultBuilder.INSTANCE;
    }
}
