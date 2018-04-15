package soft3d.v1_0;

import java.awt.AWTEvent;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.JFrame;

import soft3d.v1_0.types.*;

import static soft3d.v1_0.GLM.*;
import static soft3d.v1_0.Vector3.*;

/**
 * @author Eberhard Graether / http://egraether.com/
 * @author Mark Lundin  / http://mark-lundin.com
 */

public class TrackballControls {  
	public camera object=new camera(); Rectangle domElement=new Rectangle(0, 0);

  TrackballControls _this = this;
  static class STATE  {static int NONE= -1, ROTATE= 0, ZOOM= 1, PAN= 2, TOUCH_ROTATE= 3, TOUCH_ZOOM_PAN= 4 ;};
  public static class camera  {public vec3 position=new vec3(), up=new vec3(), target=new vec3();
public void lookAt(vec3 target2) {
copy(target, target2);}}

  public mat4 getViewMatrix(mat4 m){
	  vec3 eye=object.position, target= object.target,
				up=object.up;
	return	Matrix4.lookAt(m, eye, target, up);
  }
//API

 boolean enabled = true;

 Rectangle screen =new Rectangle(0, 0, 0, 0) ;

 float rotateSpeed = 6f;
 float zoomSpeed = 1.2f;
 float panSpeed = 0.1f;

 boolean noRotate = false;
 boolean noZoom = false;
 boolean noPan = false;
 boolean noRoll = false;

 boolean staticMoving = false;
 float dynamicDampingFactor = 0.2f;

 float minDistance = 0;
 float maxDistance = Float.POSITIVE_INFINITY;

 int[] keys = { 65 /*A*/, 83 /*S*/, 68 /*D*/ };

 // internals

 vec3 target = new vec3();

 float EPS = 0.000001f;

 vec3 lastPosition = new vec3();

 int _state = STATE.NONE,
 _prevState = STATE.NONE;

 vec3 _eye = new vec3(),

     _movePrev = new vec3(),
     _moveCurr = new vec3(),

     _lastAxis = new vec3(); float _lastAngle = 0;
     
// _rotateStart = new vec3(),
// _rotateEnd = new vec3(); 

 vec2 _zoomStart = new vec2(),
   _zoomEnd = new vec2();

 float _touchZoomDistanceStart = 0,
     _touchZoomDistanceEnd = 0;

 vec2 _panStart = new vec2(),
 _panEnd = new vec2();

 // for reset
 vec3 target0=new vec3();
// copy(target0,this.target);
 vec3 position0 =new vec3();
// copy(position0,this.object.position);
 vec3 up0 =new vec3();
// this.object.up.clone();

 // events

// var changeEvent = { type: 'change' };
// var startEvent = { type: 'start'};
// var endEvent = { type: 'end'};


 // methods

public void handleResize (int width, int height) {
//   if ( this.domElement == "document" ) {

     this.screen.x = 0;
     this.screen.y = 0;
     this.screen.width = width;
     this.screen.height = height;

//   } else {
//
//     Rectangle box = this.domElement.getBoundingClientRect();
//     // adjustments come from similar code in the jquery offset() void
//     Object d = this.domElement.ownerDocument.documentElement;
//     this.screen.x = box.x + window.pageXOffset - d.clientLeft;
//     this.screen.y = box.y + window.pageYOffset - d.clientTop;
//     this.screen.width = box.width;
//     this.screen.height = box.height;
//
//   }

 };

 public void handleEvent (AWTEvent event ) {
//   if ( typeof this[ event.type ] == 'void' ) {
//     this[ event.type ]( event );
//   }

 };

 vec2 getMouseOnScreen (float pageX,float pageY) {

   vec2 vector = new vec2();


     vector=vec2(
       ( pageX - _this.screen.x ) /_this.screen.width,
       ( pageY - _this.screen.y ) /_this.screen.height
     );

     return vector;


 }

 vec3 getMouseOnCircle   (float pageX,float pageY ) {

   vec3 vector = new vec3();
   vec3 objectUp = new vec3();
   vec3 mouseOnBall = new vec3();
   

     mouseOnBall=vec3(
       ( pageX - _this.screen.width * 0.5f - _this.screen.x ) / (_this.screen.width*.5f),
       ( _this.screen.height  +2* _this.screen.y - pageY ) / (_this.screen.width),
       0.0f
     );
return mouseOnBall;

   };


 void rotateCamera  (){

   vec3 axis = new vec3();
   Quaternion  quaternion = new Quaternion();
   vec3 eyeDirection = new vec3(),
       objectUpDirection = new vec3(),
       objectSidewaysDirection = new vec3(),
       moveDirection = new vec3();

   moveDirection=vec3( _moveCurr.x - _movePrev.x, _moveCurr.y - _movePrev.y, 0 );
     float angle = length(moveDirection);

     if ( angle !=0) {

       copy(_eye, sub(_this.object.position, _this.target) );
       eyeDirection= normalize(_eye) ;
       objectUpDirection=normalize( _this.object.up );
       objectSidewaysDirection=normalize( cross( objectUpDirection, eyeDirection ));
       
       setLength(objectUpDirection, _moveCurr.y - _movePrev.y );
       setLength(objectSidewaysDirection, _moveCurr.x - _movePrev.x );
       
       moveDirection = add(objectUpDirection, objectSidewaysDirection);
       
       axis=normalize(cross( moveDirection, _eye ));

       angle *= _this.rotateSpeed;
       quaternion.setFromAxisAngle( axis, angle );

       Quaternion.applyQuaternion(_eye, quaternion);
       Quaternion.applyQuaternion(_this.object.up, quaternion );

       copy(_lastAxis, axis );
       _lastAngle = angle;
     } else if ( !_this.staticMoving && _lastAngle!=0) {
       _lastAngle *= Math.sqrt( 1.0 - _this.dynamicDampingFactor );
       copy(_eye, sub(_this.object.position , _this.target ));
       quaternion.setFromAxisAngle( _lastAxis, _lastAngle );
       Quaternion.applyQuaternion(_eye, quaternion );
       Quaternion.applyQuaternion(_this.object.up, quaternion );
     }
     copy(_movePrev, _moveCurr );

 }

 void zoomCamera  () {

   if ( _state == STATE.TOUCH_ZOOM_PAN ) {

     float factor = _touchZoomDistanceStart / _touchZoomDistanceEnd;
     _touchZoomDistanceStart = _touchZoomDistanceEnd;
     copy(_eye, mul(factor, _eye));

   } else {

     float factor = 1.0f + ( _zoomEnd.y - _zoomStart.y ) * _this.zoomSpeed;

     if ( factor != 1.0f && factor > 0.0f ) {

//       _eye.multiplyScalar( factor );
   	  copy(_eye, mul(factor, _eye));
     }

     if ( _this.staticMoving ) {

//         _zoomStart.copy( _zoomEnd );
       copy(_zoomStart, _zoomEnd );

     } else {

       _zoomStart.y += ( _zoomEnd.y - _zoomStart.y ) * this.dynamicDampingFactor;

     }


   }

 };

 void panCamera  (){

   vec2 mouseChange = new vec2();
       vec3 objectUp = new vec3(),
     pan = new vec3();


       mouseChange=  sub( _panEnd ,_panStart );

     if ((mouseChange.x*mouseChange.x+mouseChange.y*mouseChange.y) >0 ) {

       mouseChange=mul(mouseChange, length( _eye) * _this.panSpeed );

       pan= cross( _eye , _this.object.up );setLength(pan, mouseChange.x );
       pan=add(pan, setLength(copy(objectUp, _this.object.up ), mouseChange.y ) );

       copy(_this.object.position, add(_this.object.position, pan ));
       copy(_this.target, add(_this.target, pan ));

       if ( _this.staticMoving ) {

         copy(_panStart,_panEnd );

       } else {

         mouseChange=  mul( sub( _panEnd, _panStart ),_this.dynamicDampingFactor);
         copy(_panStart, add(_panStart, mouseChange));
//         _panStart.add( mouseChange.subVectors( _panEnd, _panStart ).multiplyScalar( _this.dynamicDampingFactor ) );

       }

     }

 }

 void checkDistances  () {

   if ( !_this.noZoom || !_this.noPan ) {

     if ( lengthSquared(_eye) > _this.maxDistance * _this.maxDistance ) {

       copy(_this.object.position,   add( _this.target,setLength(_eye, _this.maxDistance )) );
       copy( _zoomStart, _zoomEnd );
//       _this.object.position.addVectors( _this.target, _eye.setLength( _this.maxDistance ) );

     }

     if ( lengthSquared(_eye) < _this.minDistance * _this.minDistance ) {

       vec3 r= add(_this.target,  setLength(_eye,_this.minDistance ) );
       copy( _this.object.position, r);
//       _this.object.position.addVectors( _this.target, _eye.setLength( _this.minDistance ) );

     }

   }

 };

 public void update   () {

   copy( _eye, sub( _this.object.position, _this.target));

   if ( !_this.noRotate ) {

     _this.rotateCamera();

   }

   if ( !_this.noZoom ) {

     _this.zoomCamera();

   }

   if ( !_this.noPan ) {

     _this.panCamera();

   }
   copy(_this.object.position, add(_this.target, _eye));
//   _this.object.position.addVectors( _this.target, _eye );

   _this.checkDistances();

   _this.object.lookAt( _this.target );

   if ( lengthSquared(sub(lastPosition , _this.object.position) ) > EPS ) {

//     _this.dispatchEvent( changeEvent );

     copy(lastPosition, _this.object.position );

   }

 };

 void reset   () {

   _state = STATE.NONE;
   _prevState = STATE.NONE;

   copy(_this.target, _this.target0 );
   copy(_this.object.position, _this.position0 );
   copy(_this.object.up, _this.up0 );

   copy(_eye,   sub(_this.object.position, _this.target )) ;

   _this.object.lookAt( _this.target );

//   _this.dispatchEvent( changeEvent );

   copy(lastPosition,  _this.object.position);
//   lastPosition.copy( _this.object.position );

 };

 // listeners

 public void keydown(AWTEvent o ) {

   KeyEvent event=(KeyEvent) o;
   int keyCode =event.getKeyCode();
   if ( _this.enabled == false ) return;

//   window.removeEventListener( "keydown", keydown );

   _prevState = _state;

   if ( _state != STATE.NONE ) {

     return;

   } else if ( keyCode == _this.keys[ STATE.ROTATE ] && !_this.noRotate ) {

     _state = STATE.ROTATE;

   } else if ( keyCode == _this.keys[ STATE.ZOOM ] && !_this.noZoom ) {

     _state = STATE.ZOOM;

   } else if ( keyCode == _this.keys[ STATE.PAN ] && !_this.noPan ) {

     _state = STATE.PAN;

   }

 }

 void keyup(AWTEvent event ) {

   if ( _this.enabled == false ) return;

   _state = _prevState;

//   window.addEventListener( 'keydown', keydown, false );

 }

 void mousedown(AWTEvent o ) {

   MouseEvent event=(MouseEvent) o;
   int pageX= event.getX(),pageY = event.getY();
   if ( _this.enabled == false ) return;

//   event.preventDefault();
//   event.stopPropagation();

   if ( _state == STATE.NONE ) {

	   if(event.getButton()!=MouseEvent.BUTTON1)
		   _state = STATE.PAN;
	   else
      	_state = STATE.ROTATE;

   }

   if ( _state == STATE.ROTATE && !_this.noRotate ) {

     copy(_moveCurr, getMouseOnCircle( pageX, pageY ) );
     copy(_movePrev, _moveCurr );

   } else if ( _state == STATE.ZOOM && !_this.noZoom ) {

     copy( _zoomStart, getMouseOnScreen( pageX, pageY ) );
     copy(_zoomEnd,_zoomStart);

   } else if ( _state == STATE.PAN && !_this.noPan ) {

     copy( _panStart, getMouseOnScreen( pageX, pageY ) );
     copy(_panEnd,_panStart);

   }
//
//   document.addEventListener( 'mousemove', mousemove, false );
//   document.addEventListener( 'mouseup', mouseup, false );

//   _this.dispatchEvent( startEvent );

 }

 void mousemove(AWTEvent o ) {
   MouseEvent event=(MouseEvent) o;
   int pageX= event.getX(),pageY = event.getY();
   if ( _this.enabled == false ) return;

//   event.preventDefault();
//   event.stopPropagation();

   if ( _state == STATE.ROTATE && !_this.noRotate ) {

     copy(_movePrev,_moveCurr);
     copy( _moveCurr, getMouseOnCircle( pageX, pageY ) );

   } else if ( _state == STATE.ZOOM && !_this.noZoom ) {

     copy( _zoomEnd, getMouseOnScreen( pageX, pageY ) );

   } else if ( _state == STATE.PAN && !_this.noPan ) {

     copy(_panEnd, getMouseOnScreen( pageX, pageY ) );

   }

 }

 void mouseup(AWTEvent event ) {

   if ( _this.enabled == false ) return;

//   event.preventDefault();
//   event.stopPropagation();

   _state = STATE.NONE;

//   document.removeEventListener( 'mousemove', mousemove );
//   document.removeEventListener( 'mouseup', mouseup );
//   _this.dispatchEvent( endEvent );

 }

 void mousewheel(MouseWheelEvent event ) {

   if ( _this.enabled == false ) return;

//   event.preventDefault();
//   event.stopPropagation();
   int wheelDelta =event.getWheelRotation();
   float delta = 0;

   if ( wheelDelta !=0) { // WebKit / Opera / Explorer 9

     delta = wheelDelta / 1f;

   } /*else if ( event.detail ) { // Firefox

     delta = - event.detail / 3;

   }*/

   _zoomStart.y += delta * 0.025;
//   _this.dispatchEvent( startEvent );
//   _this.dispatchEvent( endEvent );

 }

  void touchstart(MouseEvent event ) {

    if ( _this.enabled == false ) return;
    int pageY=event.getY(), pageX=event.getX();
    int event_touches=1;
    switch ( event_touches ) {

      case 1:
        _state = STATE.TOUCH_ROTATE;
        copy(_moveCurr, getMouseOnCircle(pageX, pageY ) );
        copy(_movePrev, _moveCurr );
        break;

      case 2:
//        _state = STATE.TOUCH_ZOOM_PAN;
//		var dx = event.touches[ 0 ].pageX - event.touches[ 1 ].pageX;
//		var dy = event.touches[ 0 ].pageY - event.touches[ 1 ].pageY;
//		_touchZoomDistanceEnd = _touchZoomDistanceStart = Math.sqrt( dx * dx + dy * dy );
//
//		var x = ( event.touches[ 0 ].pageX + event.touches[ 1 ].pageX ) / 2;
//		var y = ( event.touches[ 0 ].pageY + event.touches[ 1 ].pageY ) / 2;
//		_panStart.copy( getMouseOnScreen( x, y ) );
//		_panEnd.copy( _panStart );
        break;

      default:
        _state = STATE.NONE;

    }
//    _this.dispatchEvent( startEvent );


  }

  void touchmove(MouseEvent event ) {

    if ( _this.enabled == false ) return;

//    event.preventDefault();
//    event.stopPropagation();
    int pageY=event.getY(), pageX=event.getX();

    switch ( 1 ) {

      case 1:
    	  copy(_movePrev,_moveCurr );
    	  copy(	_moveCurr, getMouseOnCircle(pageX,pageY ) );
        break;

      case 2:
//    	  var dx = event.touches[ 0 ].pageX - event.touches[ 1 ].pageX;
//			var dy = event.touches[ 0 ].pageY - event.touches[ 1 ].pageY;
//			_touchZoomDistanceEnd = Math.sqrt( dx * dx + dy * dy );
//
//			var x = ( event.touches[ 0 ].pageX + event.touches[ 1 ].pageX ) / 2;
//			var y = ( event.touches[ 0 ].pageY + event.touches[ 1 ].pageY ) / 2;
//			_panEnd.copy( getMouseOnScreen( x, y ) );
        break;

      default:
        _state = STATE.NONE;

    }

  }

  void  touchend(MouseEvent event ) {

	  if ( _this.enabled == false ) return;
	  int touches=0;
	  int pageY=event.getY(), pageX=event.getX();
		switch ( touches ) {

			case 0:
				_state = STATE.NONE;
				break;

			case 1:
				_state = STATE.TOUCH_ROTATE;
				copy(_moveCurr, getMouseOnCircle( pageX, pageY ) );
				copy(_movePrev, _moveCurr );
				break;

		}

//		_this.dispatchEvent( endEvent );

  }

  public TrackballControls(JFrame frame){
	  MouseAdapter m  = new MouseAdapter() {
		 @Override
		public void mousePressed(MouseEvent e) {
			 _this.mousedown(e);
		} 
		 @Override
		public void mouseReleased(MouseEvent e) {
			 _this.mouseup(e);
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			_this.mousemove(e);
		}
		 @Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			 _this.mousewheel(e);
		}
	};
	  
	  KeyAdapter k  = new KeyAdapter() {
		  @Override
		public void keyPressed(KeyEvent e) {
			_this.keydown(e);
		}
		  @Override
		public void keyReleased(KeyEvent e) {
			  _this.keyup(e);
		}
		};
//  this.domElement.addEventListener( 'contextmenu', void ( event ) { event.preventDefault(); }, false );
//
//  this.domElement.addEventListener( 'mousedown', mousedown, false );
//
//  this.domElement.addEventListener( 'mousewheel', mousewheel, false );
//  this.domElement.addEventListener( 'DOMMouseScroll', mousewheel, false ); // firefox
//
//  this.domElement.addEventListener( 'touchstart', touchstart, false );
//  this.domElement.addEventListener( 'touchend', touchend, false );
//  this.domElement.addEventListener( 'touchmove', touchmove, false );
//
//  window.addEventListener( 'keydown', keydown, false );
//  window.addEventListener( 'keyup', keyup, false );0.0f, 0.0f, 800.0f, 0.0f, 0.0f, -1000.0f
final vec3 eye=vec3(0.0f, 0.0f, -10.0f),  target=vec3(0.0f, 0.0f, 0.0f), up=vec3(0,1f,0);
this.target=target;
	  copy(object.position,eye); copy(object.target,target); copy(object.up,up);
	    copy(target0,target); copy(position0,this.object.position); copy(up0 , this.object.up);
  this.handleResize(1024,680);

  // force an update at start
  this.update();
  frame.addMouseListener(m);
  frame.addMouseMotionListener(m);
  frame.addMouseWheelListener(m);
  frame.addKeyListener(k);
  }
  public void setCamera(vec3 eye, vec3 target,vec3 up) {
	  this.target=target;
	  copy(object.position,eye); copy(object.target,target); copy(object.up,up);
	  copy(target0,target); copy(position0,this.object.position); copy(up0 , this.object.up);
  }
}

//THREE.TrackballControls.prototype = Object.create( THREE.EventDispatcher.prototype );
