package com.abatra.android.wheelie.update.playstore.fake;

import com.abatra.android.wheelie.update.playstore.PlayStoreAppUpdateRequestor;
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager;

import static com.google.common.base.Preconditions.checkState;

public class FakePlayStoreAppUpdateRequestor extends PlayStoreAppUpdateRequestor {

    protected FakePlayStoreAppUpdateRequestor(FakeAppUpdateManager fakeAppUpdateManager) {
        super(fakeAppUpdateManager);
    }

    protected FakeAppUpdateManager getFakeAppUpdateManager() {
        return (FakeAppUpdateManager) getAppUpdateManager();
    }

    protected void fakeUserAcceptsUpdate() {
        checkState(getFakeAppUpdateManager().isConfirmationDialogVisible());
        getFakeAppUpdateManager().userAcceptsUpdate();
    }

    protected void startFakeAppDownload() {
        getFakeAppUpdateManager().downloadStarts();
    }

    protected void fakeInstallCompletion() {
        getFakeAppUpdateManager().installCompletes();
    }

    protected void fakeInstallFailure() {
        getFakeAppUpdateManager().installFails();
    }

    protected void fakeDownloadFailure() {
        getFakeAppUpdateManager().downloadFails();
    }

}
