LOCAL_PATH       :=  $(call my-dir)
include              $(CLEAR_VARS)
LOCAL_MODULE     :=  tempUtil
LOCAL_SRC_FILES  :=  tempUtil.c
LOCAL_PROGUARD_ENABLED:= disabled
include              $(BUILD_SHARED_LIBRARY)
