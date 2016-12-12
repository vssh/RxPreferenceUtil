package com.example.rxpreferenceutils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;

import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subscriptions.Subscriptions;

/**
 * Created by varun on 29.10.16.
 */

public class RxPreferenceUtil extends PreferenceUtil {
    private Observable<String> preferenceObservable;

    public RxPreferenceUtil(Context context, @Nullable String name) {
        super(context, name);

        this.preferenceObservable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override public void call(final Subscriber<? super String> subscriber) {
                final SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
                        subscriber.onNext(key);
                    }
                };

                mPrefs.registerOnSharedPreferenceChangeListener(listener);

                subscriber.add(Subscriptions.create(new Action0() {
                    @Override public void call() {
                        mPrefs.unregisterOnSharedPreferenceChangeListener(listener);
                    }
                }));
            }
        }).share();
    }

    private Observable<String> observe(@Nullable final String key, final boolean emitOnStart) {
        return  preferenceObservable
                .startWith(Observable.just(key, key)
                        .filter(new Func1<String, Boolean>() {
                            @Override
                            public Boolean call(String s) {
                                return emitOnStart;
                            }
                        }))
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String changedKey) {
                        return key==null || key.equals(changedKey);
                    }
                })
                .onBackpressureLatest();
    }

    public Observable<Boolean> observeBoolean(final String key, final boolean defValue, final boolean emitOnStart) {
        return  observe(key, emitOnStart)
                .map(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return mPrefs.getBoolean(key, defValue);
                    }
                });
    }

    public Observable<String> observeString(final String key, final String defValue, final boolean emitOnStart) {
        return  observe(key, emitOnStart)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return mPrefs.getString(key, defValue);
                    }
                });
    }

    public Observable<Integer> observeInt(final String key, final int defValue, final boolean emitOnStart) {
        return  observe(key, emitOnStart)
                .map(new Func1<String, Integer>() {
                    @Override
                    public Integer call(String s) {
                        return mPrefs.getInt(key, defValue);
                    }
                });
    }

    public Observable<Long> observeLong(final String key, final long defValue, final boolean emitOnStart) {
        return  observe(key, emitOnStart)
                .map(new Func1<String, Long>() {
                    @Override
                    public Long call(String s) {
                        return mPrefs.getLong(key, defValue);
                    }
                });
    }

    public Observable<Float> observeFloat(final String key, final float defValue, final boolean emitOnStart) {
        return  observe(key, emitOnStart)
                .map(new Func1<String, Float>() {
                    @Override
                    public Float call(String s) {
                        return mPrefs.getFloat(key, defValue);
                    }
                });
    }

    public Observable<Set<String>> observeBoolean(final String key, final Set<String> defValue, final boolean emitOnStart) {
        return  observe(key, emitOnStart)
                .map(new Func1<String, Set<String>>() {
                    @Override
                    public Set<String> call(String s) {
                        return mPrefs.getStringSet(key, defValue);
                    }
                });
    }

    public Observable<Pair<String, Object>> observeAny() {
        return observe(null, false)
                .map(new Func1<String, Pair<String, Object>>() {
                    @Override
                    public Pair<String, Object> call(String s) {
                        return new Pair<String, Object>(s, mPrefs.getAll().get(s));
                    }
                });
    }

    public Observable<String> observeAnyWithoutVal() {
        return observe(null, false)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        return s;
                    }
                });
    }
}
