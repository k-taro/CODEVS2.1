package codevs.main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import codevs.field.Field2;
import codevs.thinknode.ThinkNode;
import codevs.thinknode.ThinkNode2;
import codevs.thinknode.ThinkNode3;
import codevs.thinknode.ThinkNode4;
import codevs.thinknode.ThinkNode5;
import codevs.thinknode.ThinkNode6;
import codevs.thinknode.ThinkNode6_1;
import codevs.thinknode.ThinkNodeFcUP;
import codevs.thinknode.ThinkNodeFcUP2;
import codevs.thinknode.ThinkNodeFcUP3;
import codevs.thinknode.ThinkNode_l_FcUP;
import codevs.thinknode.ThinkNode_l_ScoreUP;
import codevs.thinknode.ThinkNode_m_FcUP;
import codevs.thinknode.ThinkNode_s_FcUP;
import codevs.thinknode.ThinkNode_s_ScoreUP;

public class CodeVs {

	public static int Field_wid;
	public static int Field_hei;
	public static int Pack_size;
	public static int SUM;
	public static int TURN_NUM;
	
	public static int Turn;
	
	public static Pack[] pack;
	
	public static int P;
	public static int A;
	public static int B_obs;
	public static int Th;
	
	public static int hei_p_size;
	public static int Ojama;
	
	private static Scanner scan;
	
	public static MyFrame frame;
	public static FileWriter filewriter;
	
	public static long score;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		new CodeVs().start();
//		frame.dispose();
	}
	
	public CodeVs(){
		score = 0;
		int num = 0;
		File file = null;
		if(new File("/Users/can8anyone4hear8me1/Desktop/log").exists()){
			do{
				file = new File("/Users/can8anyone4hear8me1/Desktop/log/log"+String.format("%03d", num)+".txt");
				num++;
			}while(file.exists());
		}else if(new File("/home/22115/cin15163/puzzle/log").exists()){
			do{
				file = new File("/home/22115/cin15163/puzzle/log/log"+String.format("%03d", num)+".txt");
				num++;
			}while(file.exists());			
		}else if(new File("C:\\Users\\keitaro\\Desktop\\log").exists()){
			do{
				file = new File("C:\\Users\\keitaro\\Desktop\\log\\log"+String.format("%03d", num)+".txt");
				num++;
			}while(file.exists());
		}else{
			file = new File("log.txt");
//			System.out.println(file.getAbsolutePath());
		}
		try {
			filewriter = new FileWriter(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
			
//		frame = new MyFrame();
//		frame.setVisible(true);
		
		scan = new Scanner(System.in);

		Field_wid = scan.nextInt();
		Field_hei = scan.nextInt();
		Pack_size = scan.nextInt();
		SUM = scan.nextInt();
		TURN_NUM = scan.nextInt();
		
		hei_p_size = Field_hei + Pack_size;
		Ojama = SUM + 1;
		
		pack = new Pack[TURN_NUM];
		
		for(int i=0; i<TURN_NUM;i++){
			byte[][] pack_info = new byte[Pack_size][Pack_size];
			

			for(int y=0; y<Pack_size; y++){
				for(int x=0; x<Pack_size; x++){
					pack_info[x][y] = scan.nextByte();
				}
			}
			scan.next();
			pack[i] = new Pack(pack_info);
		}
		if(Field_wid == 10){
			P = 25;
			A = 400;
			B_obs = 1000;
			Th = 100;
		}else if(Field_wid == 15){
			P = 30;
			A = 213;
			B_obs = 3000;
			Th = 1000;
		}else{
			P = 35;
			A = 240;
			B_obs = 7200;
			Th = 10000;
		}		
	}
	
	public void start(){
//		ThinkNode node = new ThinkNodeFcUP2(-1, new Field2(), 0, 1, 0, null, 0, 0);
		ThinkNode node = new ThinkNode_s_FcUP(-1, new Field2(), 0, 1, 0, 0, 0);
//		ThinkNode node = new ThinkNode_l_ScoreUP(-1, new Field2(), 0, 1, 0, 0);
//		frame.print("0\n");
		node.setUP();
		for(Turn=0; Turn<TURN_NUM; Turn++){
			try{
				node = node.getNextNode();
				//			frame.print(String.valueOf(Turn) + "\n");
			}catch(Exception e){
				try {
					filewriter.write(e.getMessage());
					filewriter.flush();
					e.printStackTrace();
				} catch (IOException e1) {
					// TODO 自動生成された catch ブロック
					e1.printStackTrace();
				}
			}
		}

		try {
			filewriter.write(String.valueOf(node.Fcp1));
			filewriter.flush();
			filewriter.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
}
