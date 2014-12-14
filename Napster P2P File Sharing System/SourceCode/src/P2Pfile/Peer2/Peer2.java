package P2Pfile.Peer2;

import resource.*;

import java.util.*;
import java.io.*;


@SuppressWarnings("serial")
public class Peer2 implements Serializable{
	
	public static void main(String[] args) throws Exception{
	String filename=null;
    ArrayList<Peer> pl=null;	
	ArrayList<String> filelist=null;
	ClientRemote cr = new ClientRemote("127.0.0.1", 3000, "/src/P2Pfile/Peer2/filefolder");
	new Thread(new Peer2Server()).start();	
	filelist = cr.filelist("/src/P2Pfile/Peer2/filefolder");
	Peer p2=new Peer("peer2","/src/P2Pfile/Peer2/filefolder",filelist,"127.0.0.1",3002);
	Message ms= new Message("String", p2);
	Message ms1= new Message ("Search File", "message");
	Message ms2= new Message("Download", p2, filename );
	//performance
		long startTime,
	    finishTime,
	    elapsedTime;
		final String MESSAGE_1 = "The elapsed time was ";

		final double NANO_FACTOR = 1000000000.0;  // nanoseconds per second

		final String MESSAGE_2 = " seconds.";
	int c=0;
	while(true){
		if(c==0){
		if(cr.PeerSearch(ms.MessagePackaging("PeerSearch", p2))){
			System.out.println("Peer Exist, No need to register!");
			cr.setpeerexist(true);
		}
		else{
			System.out.println("Need to register this Peer!");
			cr.setpeerexist(false);
		}}
		else{
			if(cr.PeerSearch(ms.MessagePackaging("PeerSearch", p2))){
				cr.setpeerexist(true);
		}
		}
		System.out.println();
		System.out.println("Enter the main menu, Please select ");
		if(cr.ifhaspeer()){
			System.out.println("AddFile");
			System.out.println("SearchFile");
			System.out.println("DownloadFile");
			System.out.println("DeleteFile");
			System.out.println("UpdateFile");
			System.out.println("Exit");
		}
		else{
			System.out.println("Register");
			System.out.println("Exit");
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String text=br.readLine();
		if(cr.ifhaspeer()){
			if(text.equals("AddFile")){
				System.out.println("Adding a File");
				cr.AddExistingFile(ms.MessagePackaging("AddFile", p2));	
				p2.setFilelist(cr.filelist("/src/P2Pfile/Peer1/filefolder"));
				System.out.println(p2.getFilelist());
			}
			else if(text.equals("SearchFile")){
				System.out.println("Search File, Please Input a name:");
				filename = br.readLine();
				startTime = System.nanoTime();
				pl=cr.searchFile(ms1.MessagePackaging("FileSearch", filename));
				if(pl!=null){
					System.out.println("The following peers contain this file:");
					for(int i=0; i<pl.size();i++){
						System.out.println(pl.get(i).getName());
					}
				}
				finishTime = System.nanoTime();
				elapsedTime = finishTime - startTime;
				System.out.println (MESSAGE_1 + (elapsedTime / NANO_FACTOR) + MESSAGE_2);
			}
			else if(text.equals("DownloadFile")){
				if(pl!=null){
					System.out.println("Which node you want to reach?");
					String peername=br.readLine();
					for(int m=0; m<pl.size();m++){
						if(peername.equals(pl.get(m).getName())){
							System.out.println("You are choosing node: " + peername );
							System.out.println("Donwloading File");
							ClientRemote cr2= new ClientRemote(pl.get(m).getIpAddress(),pl.get(m).getListeningPort(),"/src/P2Pfile/Peer2/filefolder");
							cr2.downloadFile(ms2.MessagePackaging("Download", p2, filename));
							//cr.AddExistingFile(ms.MessagePackaging("AddFile", p2));
							//cr.UpdateFile(ms2.MessagePackaging("UpdateFile", p2, filename));
							//p2.setFilelist(cr.filelist("/src/P2Pfile/Peer2/filefolder"));
						    }
						if(m==pl.size()){
							System.out.println("No Peer contains the file!");
									break;									
					}
					}
					
				}
				else{
					System.out.println("Please Search A File First");
				}
				}	
			
			
		
			
			else if(text.equals("DeleteFile")){
				System.out.println("Delete a File");
				cr.DeleteFile(ms.MessagePackaging("DeleteFile", p2));
				p2.setFilelist(cr.filelist("/src/P2Pfile/Peer2/filefolder"));
			}
			
			else if(text.equals("UpdateFile")){
				System.out.println("Update a File");
			if(filename!=null){
		     cr.UpdateFile(ms2.MessagePackaging("UpdateFile", p2, filename));}
			else{
				System.out.println("Pleae input the filename your want to update if you are adding manually");
				String filename1=br.readLine();
				cr.UpdateFile(ms2.MessagePackaging("UpdateFile", p2, filename1));
				
			}
		     p2.setFilelist(cr.filelist("/src/P2Pfile/Peer2/filefolder"));					
			}
						
			else if(text.equals("Exit")){
				System.out.println("Exit the System");
				System.exit(0);
			}
			else{
				System.out.println("Wrong Input, please input again");
				
			}
		}else{
			if(text.equals("Register")){
		
			System.out.println("Register a Peer!");
			cr.Register(ms.MessagePackaging("PeerRegister", p2));	

			}
			if(text.equals("Exit")){
				System.out.println("Exit the System");
				System.exit(0);
			}//else{
				//System.out.println("Wrong Input, please input again");
			//}
			}
		
		c++;	
	//br.close();
	
	}
	}
}