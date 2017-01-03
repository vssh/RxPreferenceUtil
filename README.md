# RxPreferenceUtil
Utility functions for Android SharedPreferences including RxJava wrapper for change notifications

## Static usage
`PreferenceUtil` can be used statically to get and set values in the default SharedPreferences.

```java
boolean exists = PreferenceUtil.contains(context, key);

boolean val = PreferenceUtil.getBoolean(context, key, defaultVal);
int val = PreferenceUtil.getInt(context, key, defaultVal);
String val = PreferenceUtil.getString(context, key, defaultVal);
long val = PreferenceUtil.getLong(context, key, defaultVal);
float val = PreferenceUtil.getFloat(context, key, defaultVal);
Set<String> val = PreferenceUtil.getStringSet(context, key, defaultVal);

PreferenceUtil.putBoolean(context, key, val);
PreferenceUtil.putInt(context, key, val);
PreferenceUtil.putString(context, key, val);
PreferenceUtil.putLong(context, key, val);
PreferenceUtil.putFloat(context, key, val);
PreferenceUtil.putStringSet(context, key, val);

PreferenceUtil.removePreference(context, key);
```

## Normal usage
For regular usage, you can create a `PreferenceUtil` object giving it the name of the preferences. If the name is null, default preferences are used.

**Note: Remember, unlike the static usage, you need to save preferences after you put them. Otherwise, the changes will be lost.**

```java
Preferenceutil pref = new PreferenceUtil(context, name);    //if name is null, default preferences are used

boolean exists = pref.contains(key);

boolean val = pref.getBoolean(key, defaultVal);
int val = pref.getInt(key, defaultVal);
String val = pref.getString(key, defaultVal);
long val = pref.getLong(key, defaultVal);
float val = pref.getFloat(key, defaultVal);
Set<String> val = pref.getStringSet(key, defaultVal);

pref.putBoolean(key, val);
pref.putInt(key, val);
pref.putString(key, val);
pref.putLong(key, val);
pref.putFloat(key, val);
pref.putStringSet(key, val);
pref.save();                    //remember to save after put, will not save otherwise

pref.removePreference(key);
```

## Change Notifications
If you need change notifications on the preferences, create instance of `RxPreferenceUtil` instead of `PreferenceUtil`.
When observing any key, you can observe any single key using `observeBoolean`, `observeInt`, `observeString`, `observeLong`, `observeFloat`, `observeStringSet`. The parameter `emitOnStart`, if set to true, will emit a notification on subscription so you can get the initial value.
You can also use `observeAny` or `observeAnyWithoutVal`. With these, you will get a notification on any change in the preferences.

```java
RxPreferenceUtil pref = new RxPreferenceUtil(context, name);

Subscription subscription = pref.observeBoolean(key, defaultVal, emitOnStart)
                                .subscribe(new Action1<String>() {
                                    @Override
                                    public void call(Boolean val) {
                                        boolean newVal = val;
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        //handle error
                                    }
                                });
                                
Subscription subscription = pref.observeAny()
                                .subscribe(new Action1<String>() {
                                    @Override
                                    public void call(Pair<String, Object> pair) {
                                        if(pair.first == someKey) {
                                            String someVal = (String) pair.second;
                                        }
                                        else if(pair.first == otherKey) {
                                            Integer otherVal = (Integer) pair.second;
                                        }
                                    }
                                }, new Action1<Throwable>() {
                                    @Override
                                    public void call(Throwable throwable) {
                                        //handle error
                                    }
                                });
```