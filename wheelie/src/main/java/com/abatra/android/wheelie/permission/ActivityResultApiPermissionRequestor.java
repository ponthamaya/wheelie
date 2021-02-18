package com.abatra.android.wheelie.permission;

import android.content.ActivityNotFoundException;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;

import com.abatra.android.wheelie.lifecycle.ILifecycleOwner;

import java.util.Optional;

import static androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale;

public class ActivityResultApiPermissionRequestor implements PermissionRequestor {

    private ActivityResultLauncher<String> singlePermissionActivityResultLauncher;

    @Nullable
    private SinglePermissionRequestCallbackDelegator singlePermissionRequestCallbackDelegator;

    @Nullable
    private ILifecycleOwner lifecycleOwner;

    private final ActivityResultCallback<Boolean> singlePermissionActivityResultCallback = result -> {
        Optional<SinglePermissionRequestCallbackDelegator> requestCallback = getSinglePermissionRequestCallbackDelegator();
        requestCallback.ifPresent(callback -> {
            if (result) {
                callback.onPermissionGranted();
            } else {
                Optional<ILifecycleOwner> ownerOptional = getLifecycleOwner();
                ownerOptional.ifPresent(lo -> {
                    boolean showRationaleAfterRequest = shouldShowRequestPermissionRationale(lo.getActivity(), callback.getPermission());
                    if (showRationaleAfterRequest) {
                        callback.onPermissionDenied();
                    } else {
                        callback.onPermissionPermanentlyDenied();
                    }
                });
            }
        });
    };

    @VisibleForTesting
    Optional<ILifecycleOwner> getLifecycleOwner() {
        return Optional.ofNullable(lifecycleOwner);
    }

    @VisibleForTesting
    Optional<SinglePermissionRequestCallbackDelegator> getSinglePermissionRequestCallbackDelegator() {
        return Optional.ofNullable(singlePermissionRequestCallbackDelegator);
    }

    @Override
    public void observeLifecycle(ILifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        this.lifecycleOwner.getLifecycle().addObserver(this);
    }

    @Override
    public void onCreate() {
        singlePermissionActivityResultLauncher = lifecycleOwner.getActivity().registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                singlePermissionActivityResultCallback);
    }

    @Override
    public void requestSystemPermission(String permission,
                                        SinglePermissionRequestCallback singlePermissionRequestCallback) {
        if (isPermissionGranted(permission)) {
            singlePermissionRequestCallback.onPermissionGranted();
        } else {
            singlePermissionRequestCallbackDelegator = new SinglePermissionRequestCallbackDelegator(permission,
                    singlePermissionRequestCallback);
            try {
                singlePermissionActivityResultLauncher.launch(permission);
            } catch (ActivityNotFoundException e) {
                singlePermissionRequestCallbackDelegator.onPermissionHandlerActivityNotFound();
            }
        }
    }

    private boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(lifecycleOwner.getContext(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onDestroy() {
        singlePermissionRequestCallbackDelegator = null;
        singlePermissionActivityResultLauncher = null;
        lifecycleOwner = null;
    }

    /* Testing */

    void setSinglePermissionActivityResultLauncher(ActivityResultLauncher<String> singlePermissionActivityResultLauncher) {
        this.singlePermissionActivityResultLauncher = singlePermissionActivityResultLauncher;
    }

    ActivityResultCallback<Boolean> getSinglePermissionActivityResultCallback() {
        return singlePermissionActivityResultCallback;
    }

    private static class SinglePermissionRequestCallbackDelegator implements SinglePermissionRequestCallback {

        private final String permission;
        private final SinglePermissionRequestCallback delegate;

        private SinglePermissionRequestCallbackDelegator(String permission, SinglePermissionRequestCallback delegate) {
            this.permission = permission;
            this.delegate = delegate;
        }

        @Override
        public void onPermissionGranted() {
            delegate.onPermissionGranted();
        }

        @Override
        public void onPermissionDenied() {
            delegate.onPermissionDenied();
        }

        @Override
        public void onPermissionHandlerActivityNotFound() {
            delegate.onPermissionHandlerActivityNotFound();
        }

        @Override
        public void onPermissionPermanentlyDenied() {
            delegate.onPermissionPermanentlyDenied();
        }

        public String getPermission() {
            return permission;
        }
    }
}
