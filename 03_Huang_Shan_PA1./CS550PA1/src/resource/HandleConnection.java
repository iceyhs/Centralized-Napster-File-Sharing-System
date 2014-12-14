package resource;

import java.net.*;
import java.io.*;
import java.util.*;
import java.nio.channels.*;


public class HandleConnection implements Runnable {
     String location;
	Socket sockethandler;
	
	
	public HandleConnection(Socket sockethandler, String location) {
		// TODO Auto-generated constructor stub
		this.sockethandler=sockethandler;
		this.location=location;
	}
	
	//get a file list， all the file
	public ArrayList<String> getfilelist(){
		
	 File directory = new File (System.getProperty("user.dir"));
	 ArrayList<String> filelist = new ArrayList<String>();
	 
	 try {
		 directory= new File(directory.getAbsolutePath()+location);
		 File fl[]=directory.listFiles();
		 for (int i =0; i<fl.length; i++){
		  filelist.add(fl[i].getName());
			 
		 }
	 }catch(Exception e){
		 e.printStackTrace();
	 }
		
		return filelist;		
	}
	
	
	//public Peer GetPeer(ArrayList<String> line, int n,int m){
	//	ArrayList<String> filelist = new ArrayList<String>();
	//	Peer p = new Peer(location, location, filelist, location, 0);
	//	p.setName(line.get(6*n));
	//	p.location=line.get(6*n+1);
	//	p.ListeningPort=Integer.parseInt(line.get(6*n+2));
	//	p.IpAddress=line.get(6*n+3);
	//	
	//	for(int i=0;i<m-4;i++){
	//		filelist.add(line.get(6*n+4+i));
	//	}
		
	//	p.filelist=filelist;
		
	//	return p;		
	//}
	public Peer GetPeer(ArrayList<String> line){
		ArrayList<String> filelist = new ArrayList<String>();
		Peer p = new Peer(location, location, filelist, location, 0);
		p.setName(line.get(0));
		p.location=line.get(1);
		p.ListeningPort=Integer.parseInt(line.get(2));
		p.IpAddress=line.get(3);
		
		for(int i=4;i<line.size();i++){
			filelist.add(line.get(i));
		}
		
		p.filelist=filelist;
		
		return p;		
	}
	
	
	
	
	@SuppressWarnings("resource")
	@Override
	public void run(){
		// TODO Auto-generated method stub
    
		//File Dir= new File (System.getProperty("user.dir"));

		
		
		
		try{ 
			
			ObjectOutputStream StreamWriter= new ObjectOutputStream(sockethandler.getOutputStream());
			ObjectInputStream StreamReader= new ObjectInputStream(sockethandler.getInputStream());
			Message messageOut = null;
			Message messageIn=(Message)StreamReader.readObject();
		    
			
			//handle the request

			   /**
				 * PeerSearch
				 */
				
				
				
			if(messageIn.RequestFunction.equals("PeerSearch")){
				Message ms = new Message ("PeerSearch", true);
				//record peer information
				File peerinfo= new File(System.getProperty("user.dir") + location + "/" + messageIn.peer.name + ".d");
				
				if(peerinfo.exists()){
					messageOut=ms.MessagePackaging("PeerSearch", true);
					StreamWriter.writeObject(messageOut);
					StreamWriter.flush();
					
				}
				else{
					System.out.println(messageIn.peer.name + "needs to register first! ");
					messageOut=ms.MessagePackaging("PeerSearch", false);
					StreamWriter.writeObject(messageOut);
					StreamWriter.flush();
				}
			}
			
			
			
			
			
			
			
			/**
			 * PeerRegister
			 */
			
			
			
		    if(messageIn.RequestFunction.equals("PeerRegister")){
				String request="PeerRegister";
		    	String message1="Register Peer Successfully!";
		    	String message2="Registering Unsuccessfully!";
		    	Message mg= new Message (request,message1);
				File dfile = new File(System.getProperty("user.dir")+ location + "/" + messageIn.peer.name +".d");
				if(dfile.createNewFile()){
					try{
				//		//appending to a file
				//		PrintWriter pw = new PrintWriter( new FileWriter(dfile,true));
				//		pw.println(messageIn.peer.name);
				//		pw.println(messageIn.peer.location);
					
				//	    String s = String.valueOf(messageIn.peer.ListeningPort);
				//		pw.println(s);
				//		pw.println(messageIn.peer.IpAddress);
						
						//for(int i=0; i<messageIn.peer.filelist.size();i++){
							//pw.println(messageIn.peer.filelist.get(i));
						//}
				//		pw.close();
					   OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(dfile),"UTF-8");
					   BufferedWriter writer=new BufferedWriter(write);
					   writer.write(messageIn.peer.name+"\n");
					   writer.write(messageIn.peer.location+"\n");
					   String s = String.valueOf(messageIn.peer.ListeningPort);
					   writer.write(s+"\n");
					   writer.write(messageIn.peer.IpAddress+"\n");
					   writer.close();
					messageOut= mg.MessagePackaging(request,message1);
					//messageOut.message="Register Peer Successfully!";
					StreamWriter.writeObject(messageOut);
					StreamWriter.flush();
					}catch(Exception e){
						e.printStackTrace();
					}
				}
				else{
					messageOut=mg.MessagePackaging(request,message2);
					StreamWriter.writeObject(messageOut);
					StreamWriter.flush();
				}
				
			}
			
			
			
			
			
		
			
		    /**
		     * Downloading file
		     */
		    
		    if(messageIn.RequestFunction.equals("Download")){
		    	byte[][] tempbyte = null;
		    	int[] byteread = null ;
		    	Message mg= new Message(messageIn.RequestFunction,messageIn.filename,byteread, tempbyte);
		    	File file=new File("");
		    	file = new File(file.getAbsoluteFile()+location+"/"+messageIn.filename);
		    	Thread.sleep(3000);
		    	FileInputStream fis= new FileInputStream(file);
		    	FileChannel fc = fis.getChannel();
		    	FileLock flock = null;
		    	while(true) {
		    		   flock = fc.tryLock(0, Long.MAX_VALUE, true); 
		    		   if(flock!=null) break;
		    		   else 
		    		      Thread.sleep(1000);
		    		}
		    	
		    	if(flock!=null){
		    	 System.out.println("server: Uploading File:" + file.getName());	
		         tempbyte= new byte[1024][1024];
		          byteread = new int[1024];
		    	 int m=0;
		    	 while((byteread[m]=fis.read(tempbyte[m]))!=-1){
		    		 m++;
		    	 }
		    	}
		    	
		    	System.out.println(messageIn.RequestFunction+file.getName());
		    	Thread.sleep(3000);
		    	messageOut=mg.MessagePackaging(messageIn.RequestFunction, file.getName(), byteread, tempbyte);
		    	StreamWriter.writeObject(messageOut);
		    	StreamWriter.flush();
		    	
		    	System.out.println("Finish Downloading file " + messageIn.filename);
		    	flock.release();
		    	fis.close();
			}
		    	
		  
		    	
		    	
		    	
		  /**
		   * Search a File
		   */
		    	
		   if (messageIn.RequestFunction.equals("FileSearch")){
			   String S=null;
			   Message me= new Message ("FileSearch","message");
			   ArrayList<String> filelist = this.getfilelist() ;
			   ArrayList<Peer> peerlist = new ArrayList<Peer> ();
			   ArrayList<String> line = new ArrayList<String>();
			   boolean find = false;
			   //for(int i =0; i<filelist.size();i++){
				//   File dfile= new File(System.getProperty("user.dir")+location + "/"+ filelist.get(i));
				   //read .d file
		     //	  BufferedReader reader= new BufferedReader(new FileReader(dfile));
		    // 	  while((S=reader.readLine())!=null){
			//	  line.add(S.replaceAll("\n", ""));
		    // 	  }
			//   reader.close();
			//   }
			//   System.out.println(messageIn.message);
			//   System.out.println(line);
		//	   Thread.sleep(1000);
			//   for(int i =0; i<filelist.size();i++){
		//		   for(int n=4+i*line.size()/filelist.size(); n<(i+1)*line.size()/filelist.size();n++){
			//		   if(line.get(n).equals(messageIn.message)){
		//				   peerlist.add(this.GetPeer(line, i,line.size()/filelist.size()));
			//			   System.out.println("File Found!");
			//			   find=true;
		//			   }
		//		   }
			   for(int i =0; i<filelist.size();i++){
				   File dfile= new File(System.getProperty("user.dir")+location + "/"+ filelist.get(i));
				   //read .d file
		     	  BufferedReader reader= new BufferedReader(new FileReader(dfile));
		     	  
		     	  while((S=reader.readLine())!=null){
				  line.add(S.replaceAll("\n", ""));
		     	  }
			   
			   
			   for(int n=4; n< line.size();n++){
				   if(line.get(n).equals(messageIn.message)){
					   peerlist.add(this.GetPeer(line));
					   System.out.println("File Found!");
					   find=true;
					   break;
				   }
			   }
			   line.clear();
			   }
			   if(find==false){
				   
				   messageOut=me.MessagePackaging("FileSearch", "file is not found");
			   }
			   else{
				   messageOut=me.MessagePackaging("FileSearch", peerlist, "file is found");
			   }
			 StreamWriter.writeObject(messageOut);
			 StreamWriter.flush();	
			   
		   }
	
		   
		   
		   /**
		    * Add a File
		    * 
		    */
		   
		   
		   if(messageIn.RequestFunction.equals("AddFile")){
			   String request= "AddFile";
			   String message1="Adding Successfully";
			   String message2="Adding Unsuccessfully";
			   Message me1= new Message (request,message1);
			  if(messageIn.message.equals("File Created")) {
				  try{
					  File dfile = new File(System.getProperty("user.dir")+location+"/"+messageIn.peer.name + ".d");
					  //PrintWriter pw = new PrintWriter(new FileWriter(dfile,true));
					  //for(int i=0;i<messageIn.peer.filelist.size();i++)
					  //{pw.println(messageIn.peer.filelist.get(i));}
					  //pw.close();
					 
					  if (!dfile.exists()) {
						    dfile.createNewFile();
						   }
						   OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(dfile,true),"UTF-8");
						   BufferedWriter writer=new BufferedWriter(write);
						   for(int i=0;i<messageIn.peer.filelist.size();i++){
							   writer.write(messageIn.peer.filelist.get(i)+"\n");
						   }
						   writer.close();
						   messageOut=me1.MessagePackaging(request,message1);
						   //System.out.println(messageOut.message);
						//messageOut.message="Adding Successfully!";
					  StreamWriter.writeObject(messageOut);
					  StreamWriter.flush();
					 // System.out.println(me1.message);
					}catch(Exception e){
						e.printStackTrace();
					}
			   }else{
   				messageOut=me1.MessagePackaging(request, message2); 
				   StreamWriter.writeObject(messageOut);
				   StreamWriter.flush();
			   }
			   
		   }
		   
		/**
		 * Update a File of downloading 
		 * 
		 */
		   
		  if(messageIn.RequestFunction.equals("UpdateFile")){
			  String request= "UpdateFile";
			   String message1="Updating Successfully";
			   String message2="Updating Unsuccessfully";
			  Message me2= new Message (request,message1);
             if(messageIn.message.equals("File Created")){
             File dfile = new File(System.getProperty("user.dir")+location+"/"+messageIn.peer.name + ".d");
              if(dfile.exists()){
            	  PrintWriter pw = new PrintWriter(new FileWriter(dfile,true));
            	  //int n = messageIn.peer.filelist.size();
            	  pw.write( messageIn.filename+"\n");
            	  pw.close();
              }
              messageOut=me2.MessagePackaging(request, message1);
              StreamWriter.writeObject(messageOut);
              StreamWriter.flush();
         }else{
            	 
          messageOut=me2.MessagePackaging(request, message2);
           StreamWriter.writeObject(messageOut);
           StreamWriter.flush();
             }

		  }
		   
		 		   
		   /**
		    * Delete a File
		    * 
		    */
		   
		   if(messageIn.RequestFunction.equals("DeleteFile")){
			   Message me2= new Message ("DeleteFile","message"); 
			if(messageIn.message.equals("Can not delete the file!")||messageIn.message.equals("Can not find the File")){
				messageOut=me2.MessagePackaging("DeleteFile", "No Change");
				StreamWriter.writeObject(messageOut);
				StreamWriter.flush();
			}else{
				File dfile = new File(System.getProperty("user.dir")+location+"/"+messageIn.peer.name + ".d");
				BufferedReader Br=new BufferedReader(new FileReader(dfile));
				String temp=null;
				ArrayList<String> line= new ArrayList<String>();
				while ((temp=Br.readLine())!=null){
					
					if(!temp.equals(messageIn.message)){
						line.add(temp);
					}
				}
				PrintWriter pw = new PrintWriter(new FileWriter(dfile));
				for(int n=0; n<line.size();n++){
					pw.println(line.get(n));
				}
				messageOut = me2.MessagePackaging("DeleteFile", "Delete Complete from Server");
				StreamWriter.writeObject(messageOut);
				pw.close();
				Br.close();
			}
		   }
		 StreamWriter.close();   	
		  StreamReader.close(); 
		}catch(Exception e)
		{
			System.out.println("ERROR Handling! ");
			e.printStackTrace();
		}
		
	}	
			
}	
		
	


