package id.indogaro.messenger.car;

import androidx.annotation.NonNull;
import androidx.car.app.CarAppService;
import androidx.car.app.Session;
import androidx.car.app.validation.HostValidator;

import id.indogaro.messenger.BuildVars;

public class IndogaroCarAppService extends CarAppService {

    @NonNull
    @Override
    public HostValidator createHostValidator() {
        if (BuildVars.DEBUG_VERSION) {
            return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR;
        }
        // Production: only Google-published Auto/Automotive hosts (signed by Google).
        return new HostValidator.Builder(getApplicationContext())
                .addAllowedHosts(androidx.car.app.R.array.hosts_allowlist_sample)
                .build();
    }

    @NonNull
    @Override
    public Session onCreateSession() {
        return new IndogaroCarSession();
    }
}
