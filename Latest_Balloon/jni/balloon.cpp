//PLEASE WORK

#include <jni.h>
#include <errno.h>

#include <vector>
#include <EGL/egl.h>
#include <GLES/gl.h>

#include "NDKHelper.h"
#include <android/sensor.h>
#include <android/log.h>
#include <android_native_app_glue.h>
#include <android/native_window_jni.h>
#include <cpu-features.h>

#define LOGI(...) ((void)__android_log_print(ANDROID_LOG_INFO, "balloon", __VA_ARGS__))
#define LOGW(...) ((void)__android_log_print(ANDROID_LOG_WARN, "balloon", __VA_ARGS__))

//------------------------------------------------------------------

struct android_app;
class engine
{
	float Vx;
	float Vy;
	bool _hasFocus;

	//Won't be used but implemented here anyways
	ndkHelper::DoubletapDetector dtDetector;
	ndkHelper::PinchDetector pDetector;
	ndkHelper::DragDetector dDetector;
	ndkHelper::perfMonitor monitor;
    ndkHelper::tapCamera _tapCamera;

	android_app* _app;

	ASensorManager* _sensorManager;
	const ASensor* _accelSensor;
	ASensorEventQueue* _sensorEventQueue;

	void showUI();
	void moveBalloon(); //Change parameters

public:
	static void handleCmd(struct android_app* app, int32_t cmd);
	//Won't really do anything now since we aren't handling inputs yet
	static int32_t handleInput(android_app* app, AInputEvent* event);

	engine();
	~engine();
	void setState(android_app* state);
	int initDisplay();
	void drawFrame();
	void termDisplay();
	bool isReady();
	void updateDirection(); //Change parameters

	void initSensors();
	void processSensors(int32_t id);
    void suspendSensors();
    void resumeSensors();
};
//-------------------------------------------------------------
//------Constructor & Destructor-------------------------------
engine::engine():			//Should be done
	_hasFocus(false),
	_app(NULL),
	_sensorManager(NULL),
	_accelSensor(NULL),
	_sensorEventQueue(NULL)
{
}

engine::~engine()			//Should be done
{
}
//-------------------------------------------------------------
//------Various functions--------------------------------------
void engine::setState(android_app* state)	//Should be done
{
	_app = state;
	dtDetector.setConfiguration(_app->config);
	dDetector.setConfiguration(_app->config);
	pDetector.setConfiguration(_app->config);
}

int engine::initDisplay()	//Should be done
{
	// initialize OpenGL ES and EGL

	    /*
	     * Here specify the attributes of the desired configuration.
	     * Below, we select an EGLConfig with at least 8 bits per color
	     * component compatible with on-screen windows
	     */
	   /* const EGLint attribs[] = {
	            EGL_SURFACE_TYPE, EGL_WINDOW_BIT,
	            EGL_BLUE_SIZE, 8,
	            EGL_GREEN_SIZE, 8,
	            EGL_RED_SIZE, 8,
	            EGL_NONE
	    };
	    EGLint w, h, dummy, format;
	    EGLint numConfigs;
	    EGLConfig config;
	    EGLSurface surface;
	    EGLContext context;

	    EGLDisplay display = eglGetDisplay(EGL_DEFAULT_DISPLAY);

	    eglInitialize(display, 0, 0);

	     Here, the application chooses the configuration it desires. In this
	     * sample, we have a very simplified selection process, where we pick
	     * the first EGLConfig that matches our criteria
	    eglChooseConfig(display, attribs, &config, 1, &numConfigs);

	     EGL_NATIVE_VISUAL_ID is an attribute of the EGLConfig that is
	     * guaranteed to be accepted by ANativeWindow_setBuffersGeometry().
	     * As soon as we picked a EGLConfig, we can safely reconfigure the
	     * ANativeWindow buffers to match, using EGL_NATIVE_VISUAL_ID.
	    eglGetConfigAttrib(display, config, EGL_NATIVE_VISUAL_ID, &format);

	    ANativeWindow_setBuffersGeometry(engine->app->window, 0, 0, format);

	    surface = eglCreateWindowSurface(display, config, engine->app->window, NULL);
	    context = eglCreateContext(display, config, NULL, NULL);

	    if (eglMakeCurrent(display, surface, surface, context) == EGL_FALSE) {
	        LOGW("Unable to eglMakeCurrent");
	        return -1;
	    }

	    eglQuerySurface(display, surface, EGL_WIDTH, &w);
	    eglQuerySurface(display, surface, EGL_HEIGHT, &h);

	    engine->display = display;
	    engine->context = context;
	    engine->surface = surface;
	    engine->width = w;
	    engine->height = h;
	    engine->state.angle = 0;

	    // Initialize GL state.
	    glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_FASTEST);
	    glEnable(GL_CULL_FACE);
	    glShadeModel(GL_SMOOTH);
	    glDisable(GL_DEPTH_TEST);*/
	showUI();
	return 0;
}

void engine::drawFrame() 	//Do this
{
	float Vx;
	float Vy;
	//moveBalloon();
}
void engine::termDisplay()
{
	LOGI("Exiting Program");
}
bool engine::isReady()
{
	if(_hasFocus)
		return true;
	return false;
}
void engine::updateDirection() 	//Do this
{
	//boop
}
//-------------------------------------------------------------
//------Static functions---------------------------------------
void engine::handleCmd(struct android_app* app, int32_t cmd) //Should be done
{
    engine* eng = (engine*)app->userData;
    switch (cmd) {
        case APP_CMD_SAVE_STATE:
            break;
        case APP_CMD_INIT_WINDOW:
            // The window is being shown, get it ready.
            if (app->window != NULL) {
                eng->initDisplay();
                eng->drawFrame();
            }
            break;
        case APP_CMD_TERM_WINDOW:
            // The window is being hidden or closed, clean it up.
            eng->termDisplay();
            eng->_hasFocus = false;
            break;
        case APP_CMD_STOP:
            break;
        case APP_CMD_GAINED_FOCUS:
            eng->resumeSensors();
            eng->_hasFocus = true;
            break;
        case APP_CMD_LOST_FOCUS:
            eng->suspendSensors();
            eng->_hasFocus = false;
            eng->drawFrame();
            break;
	}
}
int32_t engine::handleInput(android_app* app, AInputEvent* event) //Should be done
{
    engine* eng = (engine*)app->userData;
    if (AInputEvent_getType(event) == AINPUT_EVENT_TYPE_MOTION)
    {
        ndkHelper::GESTURE_STATE doubleTapState = eng->dtDetector.detect(event);
        ndkHelper::GESTURE_STATE dragState = eng->dDetector.detect(event);
        ndkHelper::GESTURE_STATE pinchState = eng->pDetector.detect(event);

        //Double tap detector has a priority over other detectors
         if( doubleTapState == ndkHelper::GESTURE_STATE_ACTION )
         {
             //Detect double tap
             eng->_tapCamera.reset(true);
         }
         else
         {
             //Handle drag state
             if( dragState & ndkHelper::GESTURE_STATE_START )
             {
                 //Otherwise, start dragging
                 return 1;
             }
             else if( dragState & ndkHelper::GESTURE_STATE_MOVE )
             {
                 return 1;
             }
             else if( dragState & ndkHelper::GESTURE_STATE_END )
             {
                 return 1;
             }

             //Handle pinch state
             if( pinchState & ndkHelper::GESTURE_STATE_START )
             {
                 //Start new pinch
				return 1;
             }
             else if( pinchState & ndkHelper::GESTURE_STATE_MOVE )
             {
                 //Multi touch
                 //Start new pinch
				 return 1;
             }
         }
         return 1;
    }
    return 0;
}
//-------------------------------------------------------------
//------Connecting functions-----------------------------------
void engine::showUI() 	//Should be done
{
    JNIEnv *jni;
    _app->activity->vm->AttachCurrentThread(&jni, NULL);

    //Default class retrieval
    jclass clazz = jni->GetObjectClass( _app->activity->clazz );
    jmethodID methodID = jni->GetMethodID(clazz, "showUI", "()V" );
    jni->CallVoidMethod( _app->activity->clazz, methodID );

    _app->activity->vm->DetachCurrentThread();
    return;
}
void engine::moveBalloon() //Don't use this yet
{
    JNIEnv *jni;
    _app->activity->vm->AttachCurrentThread(&jni, NULL);

    //Default class retrieval
    jclass clazz = jni->GetObjectClass( _app->activity->clazz );
    jmethodID methodID = jni->GetMethodID(clazz, "moveBalloon", "(FF)V" );
    jni->CallVoidMethod( _app->activity->clazz, methodID, Vx, Vy);

    _app->activity->vm->DetachCurrentThread();
    return;
}
//-------------------------------------------------------------
//------Sensor functions---------------------------------------
void engine::initSensors() //Should be done
{
    _sensorManager = ASensorManager_getInstance();
    _accelSensor = ASensorManager_getDefaultSensor( _sensorManager,
            ASENSOR_TYPE_ACCELEROMETER);
    _sensorEventQueue = ASensorManager_createEventQueue( _sensorManager,
            _app->looper, LOOPER_ID_USER, NULL, NULL);
}
void engine::processSensors(int32_t id) //Should be done
{
    // If a sensor has data, process it now.
    if( id == LOOPER_ID_USER )
    {
        if (_accelSensor != NULL)
        {
            ASensorEvent event;
            while (ASensorEventQueue_getEvents(_sensorEventQueue,
                    &event, 1) > 0)
            {
            }
        }
    }
}
void engine::suspendSensors() //Should be done
{
    // When our app loses focus, we stop monitoring the accelerometer.
    // This is to avoid consuming battery while not being used.
    if (_accelSensor != NULL) {
        ASensorEventQueue_disableSensor(_sensorEventQueue,
                _accelSensor);
    }
}
void engine::resumeSensors() //Should be done
{
    // When our app gains focus, we start monitoring the accelerometer.
    if (_accelSensor != NULL) {
        ASensorEventQueue_enableSensor(_sensorEventQueue,
                _accelSensor);
        // We'd like to get 60 events per second (in us).
        ASensorEventQueue_setEventRate(_sensorEventQueue,
                _accelSensor, (1000L/60)*1000);
    }
}

//-------------------------------------------------------------
//-------------------------------------------------------------

engine b_engine;

void android_main(android_app* state)
{
	app_dummy();
	b_engine.setState(state);
	//ndkHelper::JNIHelper::getInstance()->init(state->activity);

	state->userData = &b_engine;
	state->onAppCmd = engine::handleCmd;
	state->onInputEvent = engine::handleInput;


#ifdef USE_NDK_PROFILER
    monstartup("libballoon.so");
#endif


    b_engine.initSensors();

    while(1)
    {
    	int id;
    	int events;
    	android_poll_source* source;
    	while ((id = ALooper_pollAll( b_engine.isReady() ? 0 : -1, NULL,
    			&events, (void**)&source) ) >= 0)
    	{
			// Process this event.
			if (source != NULL)
				source->process(state, source);

			b_engine.processSensors( id );

			// Check if we are exiting.
			if (state->destroyRequested != 0)
			{
				b_engine.termDisplay();
				return;
			}
		}
        if( b_engine.isReady() )
        {
            // Drawing is throttled to the screen update rate, so there
            // is no need to do timing here.
            b_engine.drawFrame();
        }

    }
}
