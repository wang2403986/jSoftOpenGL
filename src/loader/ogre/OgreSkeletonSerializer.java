package loader.ogre;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;

import loader.ogre.mesh.Animation;
import loader.ogre.mesh.Bone;
import loader.ogre.mesh.NodeAnimationTrack;
import loader.ogre.mesh.NodeKeyFrame;
import loader.ogre.mesh.OgreSkeleton;



import com.sun.corba.se.spi.orbutil.fsm.Input;

public class OgreSkeletonSerializer {
	 long SSTREAM_OVERHEAD_SIZE = 2 + 4;
	 int HEADER_STREAM_ID_EXT = 0x1000;
	private int mCurrentstreamLen;
	public static void main(String[] s) throws IOException {
		FileInputStream stream=new FileInputStream(System.getProperty("user.dir")+"\\models\\npc西域耍蛇人.skeleton");
		OgreSkeletonSerializer serializer=new OgreSkeletonSerializer();
		serializer.importSkeleton(stream, new OgreSkeleton());
	}
	
	 void importSkeleton(InputStream stream, OgreSkeleton pSkel) throws IOException
	    {
			// Determine endianness (must be the first thing we do!)
			//determineEndianness(stream);

			// Check header
	        readFileHeader(stream);

	        int streamID;
	        while(stream.available()>0)
	        {
	            streamID = readChunk(stream);
	            switch (streamID)
	            {
				case SKELETON_BLENDMODE:
				{
					// Optional blend mode
					int blendMode;
					blendMode=readShort(stream );
					//pSkel->setBlendMode(static_cast<SkeletonAnimationBlendMode>(blendMode));
					break;
				}
	            case SKELETON_BONE:
	                readBone(stream, pSkel);
	                break;
	            case SKELETON_BONE_PARENT:
	                readBoneParent(stream, pSkel);
	                break;
	            case SKELETON_ANIMATION:
	                readAnimation(stream, pSkel);
					break;
				case SKELETON_ANIMATION_LINK:
					readSkeletonAnimationLink(stream, pSkel);
					break;
	            }
	        }

			// Assume bones are stored in the binding pose
	       // pSkel->setBindingPose();


	    }
	//---------------------------------------------------------------------
	    void readBone(InputStream stream, OgreSkeleton pSkel) throws IOException//Skeleton
	    {
	        // char* name
	        String name = readString(stream);
	        int strLen=name.length()+1;
	        // unsigned short handle            : handle of the bone, should be contiguous & start at 0
	        int handle;
	        handle=readShort(stream);

	        // Create new bone
	        Bone pBone = new Bone();
	        pBone.handle=handle;
	        pBone.name=name;
	        if (pSkel.bones==null) {
	        	pSkel.bones=new ArrayList<Bone>();
			}
	        pSkel.bones.add(pBone);

	        // Vector3 position                 : position of this bone relative to parent 
	        float[] pos=new float[3];
	        //readObject(stream, pos);
	        for (int i = 0; i < pos.length; i++) {
				pos[i]=readFloat(stream);
			}
	        pBone.position=pos;
	        // Quaternion orientation           : orientation of this bone relative to parent 
	        float[] q=new float[4];
	        //readObject(stream, q);
	        for (int j = 0; j < q.length; j++) {
				q[j]=readFloat(stream);
			}
	        pBone.orientation=(q);
	        // Do we have scale?
	        if (mCurrentstreamLen-strLen > calcBoneSizeWithoutScale(pSkel, pBone))
	        {
	            float[] scale=new float[3];
	            for (int j = 0; j < scale.length; j++) {
	            	scale[j]=readFloat(stream);
				}
	            //readObject(stream, scale);
	            pBone.scale=(scale);
	        }
	    }
	    //---------------------------------------------------------------------
	    void readBoneParent(InputStream stream, OgreSkeleton pSkel) throws IOException//Skeleton
	    {
	        // All bones have been created by this point
	        //Bone *child, *parent;
	    	Bone child,parent;
	        int childHandle, parentHandle;

	        // unsigned short handle             : child bone
	        childHandle=readShort(stream);
	        // unsigned short parentHandle   : parent bone
	        parentHandle= readShort(stream);

	        if (pSkel.boneParent==null) {
	        	pSkel.boneParent=new ArrayList<int[]>();
			}
	       
	        // Find bones
	        parent = pSkel.getBone(parentHandle);
	        child = pSkel.getBone(childHandle);
	        
	        child.parent=parent;
	        // attach
	       if( parent.children==null)
	    	   parent.children=new ArrayList<Bone>();
	       
	       parent.children.add(child);
	    }
	    //---------------------------------------------------------------------
	    void readAnimation(InputStream stream, OgreSkeleton pSkel) throws IOException//Skeleton
	    {
	        // char* name                       : Name of the animation
	        String name;
	        name = readString(stream);
	        // float length                      : Length of the animation in seconds
	        float len;
	        /******************天龙八部3： readFloat改为readShort*******************/
	        //len=readShort(stream);
	        len=readFloat(stream);
	        

	        //Animation *pAnim = pSkel->createAnimation(name, len);
	        Animation  pAnim=new Animation();
	        if (pSkel.animations==null) {
	        	pSkel.animations=new ArrayList<Animation>();
			}
	        pSkel.animations.add(pAnim);
	        pAnim.name=name;
	        pAnim.length=len;
	        // Read all tracks
	        if (stream.available()>0)
	        {
	           int streamID = readChunk(stream);
				// Optional base info is possible
				if (streamID == SKELETON_ANIMATION_BASEINFO)
				{
					// char baseAnimationName
					String baseAnimName = readString(stream);
					// float baseKeyFrameTime
					float baseKeyTime;
					baseKeyTime=readFloat(stream);
					
					//pAnim->setUseBaseKeyFrame(true, baseKeyTime, baseAnimName);
					
	                if (stream.available()>0)
	                {
	                    // Get next stream
	                    streamID = readChunk(stream);
	                }
				}
				if (streamID == 3824)
				{
//					readAnimationTrack(stream, pAnim, pSkel);
//
//	                if (stream.available()>0)
//	                {
//	                    // Get next stream
//	                    streamID = readChunk(stream);
//	                }
				}
				
	            while(streamID == SKELETON_ANIMATION_TRACK && stream.available()>0)
	            {
	            	//readAnimationTrack(stream, pAnim, pSkel);
	            	/**********************天龙八部3  改为如下************************************/
	                //readAnimationTrackTlbb3(stream, pAnim, pSkel);
	            	/**********************天龙八部1  改为如下************************************/
	            	readAnimationTrackTlbb1(stream, pAnim, pSkel);

	                if (stream.available()>0)
	                {
	                    // Get next stream
	                    streamID = readChunk(stream);
	                }
	            }
	            if (stream.available()>0)
	            {
	                // Backpedal back to start of this stream if we've found a non-track
	                stream.skip(-SSTREAM_OVERHEAD_SIZE);
	            }

	        }



	    }
	    //---------------------------------------------------------------------
	    void readAnimationTrack(InputStream stream, Animation anim, 
	            OgreSkeleton pSkel) throws IOException//Skeleton
	        {
	            // unsigned short boneIndex     : Index of bone to apply to
	           int boneHandle;
	           boneHandle=readShort(stream);

	            // Find bone
	            Bone targetBone = pSkel.getBone(boneHandle);

	            // Create track
	           NodeAnimationTrack pTrack =new NodeAnimationTrack();
	           anim.nodeAnimationTracks.add(pTrack);
	           pTrack.bone=targetBone;

	            // Keep looking for nested keyframes
	            if (stream.available()>0)
	            {
	                int streamID = readChunk(stream);
	                //streamID == SKELETON_ANIMATION_TRACK_KEYFRAME &&
	                
	                while((streamID == SKELETON_ANIMATION_TRACK_KEYFRAME 
	                		||streamID==0x4120//天龙八部1使用
	                		)&& stream.available()>0)//16658  14,,16656 0
	                {
	                	//stream.skip(mCurrentstreamLen-SSTREAM_OVERHEAD_SIZE);
	                    readKeyFrame(stream, pTrack, pSkel);

	                    if (stream.available()>0)
	                    {
	                        // Get next stream
	                        streamID = readChunk(stream);
	                    }
	                }
	                if (stream.available()>0)
	                {
	                    // Backpedal back to start of this stream if we've found a non-keyframe
	                    stream.skip(-SSTREAM_OVERHEAD_SIZE);
	                }

	            }


	        }
	    void readAnimationTrackTlbb1(InputStream stream,Animation anim, OgreSkeleton pSkel) throws IOException//Skeleton
	    {
	    	 // unsigned short boneIndex     : Index of bone to apply to
	           int boneHandle;
	           boneHandle=readShort(stream);

	            // Find bone
	            Bone  targetBone = pSkel.getBone(boneHandle);

	          
	            // Create track
	           // NodeAnimationTrack* pTrack = anim->createNodeTrack(boneHandle, targetBone);
	            NodeAnimationTrack  pTrack = new NodeAnimationTrack();
	            pTrack.bone=targetBone;
	            if (anim.nodeAnimationTracks==null) {
	            	anim.nodeAnimationTracks=new ArrayList<NodeAnimationTrack>();
				}
	            anim.nodeAnimationTracks.add(pTrack);
	            if (stream.available()>0)
	            {
	               int streamID = readChunk(stream);
	                while( (streamID == SKELETON_ANIMATION_TRACK_KEYFRAME || 
	                streamID == 0x4120)//天龙八部1使用
	                    && stream.available()>0)
	                {
	                    if (streamID == 0x4120)
	                    {
	                        // 新增代码
	                        int len;
	                        int flags;
	                        len= readShort(stream);
	                        flags= readShort(stream);

	                        float time;
	                        for (int i = 0; i < len; i += 1)
	                        {
	                        	NodeKeyFrame kf =new NodeKeyFrame();
	                        	if (pTrack.keyFrames==null) {
	                        		pTrack.keyFrames=new ArrayList<NodeKeyFrame>();
								}
	                        	pTrack.keyFrames.add(kf);
	                        	time= readFloat(stream);
	                        	kf.time=time;

	                            float[] rot =new float[4];
	                            if ((flags & 1)==1)
	                            {
	                               for (int j = 0; j < rot.length; j++) {
									rot[j]=readFloat(stream);
								}
	                            }
	                            kf.rotation=(rot);

	                            float[] trans =new float[3];
	                            if ((flags & 2)>>1==1)
	                            {
	                            	 for (int j = 0; j < trans.length; j++) {
	                            		 trans[j]=readFloat(stream);
	 								}
	                            }
	                            kf.translate=(trans);

	                            //网上流行的解析代码，少了这部分，导致有些动画解析不正确，比如柳枝的摆动
	                            //很多场景中的StaticEntity都包含柳树的骨骼动画                        
	                            float[] scale = new float[3];
	                            if ((flags & 4)>>2==1)
	                            {
	                            	for (int j = 0; j < scale.length; j++) {
	                            		scale[j]=readFloat(stream);
	 								}
	                            }
	                            kf.scale=(scale);
	                        }
	                    }
	                    else
	                    {
	                        readKeyFrame(stream, pTrack, pSkel);
	                    }

	                    if (stream.available()>0)
	                    {
	                        // Get next stream
	                        streamID = readChunk(stream);
	                    }
	                }
	               
	                if (stream.available()>0)
	                {
	                    // Backpedal back to start of this stream if we've found a non-keyframe
	                    stream.skip(-SSTREAM_OVERHEAD_SIZE);
	                }
	         //...
	    }
	    }
	        //---------------------------------------------------------------------
	    void readAnimationTrackTlbb3(InputStream stream, Animation anim, 
	            OgreSkeleton pSkel) throws IOException//Skeleton
	        {
	            // unsigned short boneIndex     : Index of bone to apply to
	           int boneHandle;
	           boneHandle=readShort(stream);

	            // Find bone
	            Bone targetBone = pSkel.getBone(boneHandle);

	            // Create track
	            NodeAnimationTrack  pTrack = new NodeAnimationTrack();
	            if (anim.nodeAnimationTracks==null) {
	            	anim.nodeAnimationTracks=new ArrayList<NodeAnimationTrack>();
				}
	            anim.nodeAnimationTracks.add(pTrack);
	            pTrack.bone=targetBone;
	          
	            // Keep looking for nested keyframes
	            if (stream.available()>0)
	            {
	            	int trackID=readChunk(stream);
	            	
	            	while (trackID == SKELETON_ANIMATION_TRACK_KEYFRAME) {
	            		float time=readShort(stream);
	            		NodeKeyFrame keyFrame=new NodeKeyFrame();
	            		keyFrame.time=time;
	            		if (pTrack.keyFrames==null) {
	            			pTrack.keyFrames=new ArrayList<NodeKeyFrame>();
						}
	            		pTrack.keyFrames.add(keyFrame);
	            		
	            		int streamID = readChunk(stream);
		                //streamID == SKELETON_ANIMATION_TRACK_KEYFRAME &&
		                while((streamID == 0x4111||streamID==0x4112||streamID==0x4113
		                		) && stream.available()>0)//16658  14,,16656 0
		                {
		                	
		                    switch (streamID) {
						
							case 0x4111://translation
								//stream.skip(12);
								float[] array=new float[3];
								for (int i = 0; i < array.length; i++) {
									array[i]=readFloat(stream);
								}
								keyFrame.translate=array;
								break;
							case 0x4112://rotate
								//stream.skip(16);
								float[] q=new float[4];
								for (int i = 0; i < q.length; i++) {
									q[i]=readFloat(stream);
								}
								keyFrame.rotation=q;
								break;
							case 0x4113:
								stream.skip(12);
								break;
							default:
								break;
							}

		                    if (stream.available()>0)
		                    {
		                        // Get next stream
		                        streamID = readChunk(stream);
		                    }
		                }
		                if (stream.available()>0)
		                {
		                    // Backpedal back to start of this stream if we've found a non-keyframe
		                    stream.skip(-SSTREAM_OVERHEAD_SIZE);
		                }
		                
		                
		                if (stream.available()>0)
	                    {
	                        // Get next stream
		                	trackID = readChunk(stream);
	                    }
					}
	            	
	            	
	            	if (stream.available()>0)
	                {
	                    // Backpedal back to start of this stream if we've found a non-keyframe
	                    stream.skip(-SSTREAM_OVERHEAD_SIZE);
	                }
	                

	            }


	        }
	        //---------------------------------------------------------------------
	    void readKeyFrame(InputStream stream, NodeAnimationTrack track, //NodeAnimationTrack
	            OgreSkeleton pSkel) throws IOException//Skeleton
	        {
	            // float time                    : The time position (seconds)
	            float time;
	            time=readFloat(stream);
	            //time=readShort(stream);

	            NodeKeyFrame kf = new NodeKeyFrame();
	            if (track.keyFrames==null) {
	            	track.keyFrames=new ArrayList<NodeKeyFrame>();
				}
	            track.keyFrames.add(kf);
	            kf.time=time;

	            // Quaternion rotate            : Rotation to apply at this keyframe
	            float[] rot=new float[4];//x,y,z,w
	            //readObject(stream, rot);
	            
	            for (int i = 0; i < rot.length; i++) {
	            	rot[i]=readFloat(stream);
				}
	            kf.rotation=(rot);
	            // Vector3 translate            : Translation to apply at this keyframe
	            float[] trans=new float[3];
	            //readObject(stream, trans);
	            
	            for (int i = 0; i < trans.length; i++) {
	            	trans[i]=readFloat(stream);
				}
	            kf.translate=(trans);
	            // Do we have scale?
	            if (mCurrentstreamLen > calcKeyFrameSizeWithoutScale(pSkel, kf))
	            {
	                float[] scale=new float[3];
	                //scale= readObject(stream, scale);
	                for (int i = 0; i < scale.length; i++) {
	                	scale[i]=readFloat(stream);
					}
	                kf.scale=(scale);
	            }
	        }
	    	//---------------------------------------------------------------------
		void readSkeletonAnimationLink(InputStream stream, 
			Object pSkel) throws IOException//Skeleton
		{
			// char* skeletonName
			String skelName = readString(stream);
			// float scale
			float scale;
			scale=readFloat(stream);

			//pSkel->addLinkedSkeletonAnimationSource(skelName, scale);

		}
		//---------------------------------------------------------------------
		int calcBoneSizeWithoutScale( Object pSkel, //Skeleton
		         Object pBone)
		    {
		        int size = (int) SSTREAM_OVERHEAD_SIZE;

		        // handle
		        //size += sizeof(unsigned short);
		        size += 2;

		        // position
		        //size += sizeof(float) * 3;
		        size += 4*3;

		        // orientation
		        //size += sizeof(float) * 4;

		        size += 4*4;
		        return size;
		    }
		    //---------------------------------------------------------------------
		int calcKeyFrameSizeWithoutScale( Object pSkel, //Skeleton
				Object pKey)//TransformKeyFrame
		    {
		        int size = (int) SSTREAM_OVERHEAD_SIZE;

		        // float time                    : The time position (seconds)
		        size += 4;
		        // Quaternion rotate            : Rotation to apply at this keyframe
		        size += 4 * 4;
		        // Vector3 translate            : Translation to apply at this keyframe
		        size += 4 * 3;

		        return size;
		    }
			//---------------------------------------------------------------------
	 private void readFileHeader(InputStream stream) throws IOException {
			int i=readShort(stream);
			String string=readString(stream);
			
		}
	 private int readChunk(InputStream stream) throws IOException {
			int i= readShort(stream);
			mCurrentstreamLen= readInt(stream);
			return i;
		}
	 
	 private int readInt(InputStream stream) throws IOException {
	    	byte[] b=new byte[4];
	    	stream.read(b);
	    	
			int i=(b[3]&0xff)<<24;
				i+=	(b[2]&0xff)<<16;
				i+=(b[1]&0xff)<<8;
				i+=(b[0]&0xff);
			return i;
		
	}
	 private int readShort(InputStream stream ) throws IOException {
	    	byte[] b=new byte[2];
	    	stream.read(b);
	    	int r= (b[1]&0xff)<<8;
	    	r+=(b[0]&0xff);
			return r;
			
		}
	 private String readString(InputStream stream) throws IOException {
	    	byte[] bytes=new byte[2];
	    	
	    	StringBuilder buffer=new StringBuilder();
	    	while (stream.available()>0) {
	    		stream.read(bytes);
	    		if (bytes[0]==0x0a) {
	    			stream.skip(-1);
					break;
				}
	    		if (bytes[1]==0x0a) {
	    			buffer.append(new String(bytes).charAt(0));
					break;
				}
	    		buffer.append(new String(bytes,Charset.forName("GBK")));
			}
			return buffer.toString();
		}
	 private float readFloat(InputStream stream) throws IOException {
	    	int bits= readInt(stream);
	    	float f= Float.intBitsToFloat(bits);
			return f;
		}
	 private boolean readBool(InputStream stream) throws IOException {
			int b= stream.read();
			if (b==1) {
				return true;
			}
			return false;
		}
	public final static int SKELETON_HEADER            = 0x1000,
	            // char* version           : Version number check
				SKELETON_BLENDMODE		   = 0x1010, // optional
					// unsigned short blendmode		: SkeletonAnimationBlendMode
			
	        SKELETON_BONE              = 0x2000,
	        // Repeating section defining each bone in the system. 
	        // Bones are assigned indexes automatically based on their order of declaration
	        // starting with 0.

	            // char* name                       : name of the bone
	            // unsigned short handle            : handle of the bone, should be contiguous & start at 0
	            // Vector3 position                 : position of this bone relative to parent 
	            // Quaternion orientation           : orientation of this bone relative to parent 
	            // Vector3 scale                    : scale of this bone relative to parent 

	        SKELETON_BONE_PARENT       = 0x3000,
	        // Record of the parent of a single bone, used to build the node tree
	        // Repeating section, listed in Bone Index order, one per Bone

	            // unsigned short handle             : child bone
	            // unsigned short parentHandle   : parent bone

	        SKELETON_ANIMATION         = 0x4000,
	        // A single animation for this skeleton

	            // char* name                       : Name of the animation
	            // float length                      : Length of the animation in seconds
			
				SKELETON_ANIMATION_BASEINFO = 0x4010,
				// [Optional] base keyframe information
				// char* baseAnimationName (blank for self)
				// float baseKeyFrameTime

	            SKELETON_ANIMATION_TRACK = 0x4100,
	            // A single animation track (relates to a single bone)
	            // Repeating section (within SKELETON_ANIMATION)
	                
	                // unsigned short boneIndex     : Index of bone to apply to

	                SKELETON_ANIMATION_TRACK_KEYFRAME = 0x4110,
	                // A single keyframe within the track
	                // Repeating section

	                    // float time                    : The time position (seconds)
	                    // Quaternion rotate            : Rotation to apply at this keyframe
	                    // Vector3 translate            : Translation to apply at this keyframe
	                    // Vector3 scale                : Scale to apply at this keyframe
			SKELETON_ANIMATION_LINK         = 0x5000
			// Link to another skeleton, to re-use its animations

				// char* skeletonName					: name of skeleton to get animations from
				// float scale							: scale to apply to trans/scale keys
			;
}
