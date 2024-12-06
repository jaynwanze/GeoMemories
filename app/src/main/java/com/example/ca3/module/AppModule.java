package com.example.ca3.module;

import android.app.Application;
import android.content.Context;
import com.example.ca3.utils.*;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import javax.inject.Singleton;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public LocationUtils provideLocationUtils(Application application) {
        return new LocationUtils(application);
    }

    @Provides
    @Singleton
    public UserPreferencesManager provideUserPreferencesManager(Application application) {
        return new UserPreferencesManager(application);
    }
}
