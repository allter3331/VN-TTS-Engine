LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_LDLIBS := -llog
LOCAL_LDLIBS    += -landroid
LOCAL_MODULE := synthtsvn
LOCAL_SRC_FILES := vnttsjni.cpp
include $(BUILD_SHARED_LIBRARY)