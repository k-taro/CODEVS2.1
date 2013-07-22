package codevs.field;

import codevs.main.CodeVs;

/**
 * 一番早い
 * @author Keitaro Wakabayashi
 *
 */
public class Field2 extends Field {
	
	public Field2(){
		super();
	}
	
	protected Field2(Field f){
		super(f);
	}
	
	@Override
	public Field2 clone(){
		return (Field2) new Field2(this);
	}
	
	@Override
	protected int eliminate(){
		//System.out.print("Field2\n");
		byte[][] temp = copyFieldInfo();
		int E = 0;
		
		int[] dir_x = {1,1,0,-1};
		int[] dir_y = {0,1,1, 1};
		
		int[] dir_x2 = { 0,-1, 1,-1, 1, 1,-1, 0};
		int[] dir_y2 = {-1,-1,-1, 0, 0, 1, 1, 1};

		for(int x = 0; x<CodeVs.Field_wid; x++){
			for(int y = 0; y<CodeVs.hei_p_size; y++){

				byte f = field[x][y];
				if(f != 0 && f != CodeVs.Ojama){
					for(int d = 0; d<4; d++){
						int xx = x;
						int yy = y;
						int sum = f;
						
						while(true){
							if(sum > CodeVs.SUM){
								break;
							}else if(sum < CodeVs.SUM){
								xx += dir_x[d];
								yy += dir_y[d];

								if(yy < CodeVs.hei_p_size && 0<=xx && xx < CodeVs.Field_wid ){
									byte ff = field[xx][yy];
									if(ff == 0 || ff == CodeVs.Ojama){
										break;
									}
									sum += ff;
								}else{
									break;
								}
							}else{ // sum == CodeVs.SUM
								int xxx = x;
								int yyy = y;
								
								temp[xxx][yyy] = 0;
								E++;
								
								for(int d2 = 0; d2<7; d2++){
									int xxxx = xxx+dir_x2[d2];
									int yyyy = yyy+dir_y2[d2];
									if(0<=xxxx && xxxx<CodeVs.Field_wid && 0<=yyyy && yyyy <CodeVs.hei_p_size && temp[xxxx][yyyy] == CodeVs.Ojama){
										temp[xxxx][yyyy] = 0;
										E++;
									}
								}
								
								while(xxx != xx || yyy != yy){
									xxx += dir_x[d];
									yyy += dir_y[d];
									temp[xxx][yyy] = 0;
									E++;
									for(int d2 = 1; d2<8; d2++){
										int xxxx = xxx+dir_x2[d2];
										int yyyy = yyy+dir_y2[d2];
										if(0<=xxxx && xxxx<CodeVs.Field_wid && 0<=yyyy && yyyy <CodeVs.hei_p_size && temp[xxxx][yyyy] == CodeVs.Ojama){
											temp[xxxx][yyyy] = 0;
											E++;
										}
									}
								}
								break;
							}

						}
					}
				}
				
			}	
		}
		field = temp;
		return E;
	}
}
