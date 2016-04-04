package com.udacity.footballscores.service;

import android.content.Intent;
import android.widget.RemoteViewsService;

import com.udacity.footballscores.WidgetDataProvider;

/**
 * Created by TechSutra on 4/3/16.
 */
public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetDataProvider(this, intent);
    }
}
