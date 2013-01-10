package com.seikoudoku2000.hatebumap.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.seikoudoku2000.hatebumap.batch.player.HatebuInfoGetter;
import com.seikoudoku2000.hatebumap.batch.player.KeyPhraseExtractor;
import com.seikoudoku2000.hatebumap.batch.player.LocalSearcher;
import com.seikoudoku2000.hatebumap.batch.player.WebPageParser;
import com.seikoudoku2000.hatebumap.batch.player.HatebuInfoGetter.HatebuInfoDto;

/**
 * DB�ɓ������邽�߂�CSV�𐶐����邽�߂̃o�b�`�����B
 * @author tomitayousuke
 *
 */
public class CreateData {

	public static void main(String[] args) throws Exception {
		
		//�����ɃC���v�b�g�t�@�C����z�u
		String inFileName = "url_invest_auto.txt"; 
		
		//"MMddHHmm_�C���v�b�g�t�@�C����"�̃t�@�C�����o�͂����B
		String outFileName = new SimpleDateFormat("MMddHHmm").format(new Date()) + "_" + inFileName;
		
		BufferedReader bufferReader = null; 
		String str = ""; 
		try { 
			bufferReader = new BufferedReader(new InputStreamReader(new FileInputStream(inFileName),"UTF-8")); 

			//��s���ǂݍ��� 
			while ((str = bufferReader.readLine()) != null) { 
				System.out.println(str);

				String[] strArray = str.split("\t");
				if(strArray.length != 3) {
					continue;
				} else {
					String nextUrl = strArray[0];
					
					//�͂Ăȃu�b�N�}�[�N���̎擾
					//int hatebuCount = HatebuCountChecker.getHatebuCount(nextUrl);
					HatebuInfoDto hatebuDto = HatebuInfoGetter.getHatebuInfoLite(nextUrl);
					
					//�y�[�W�̉��-�����ꒊ�o-POI�f�[�^�̎擾
					Set<String> spotSet = 
						LocalSearcher.getPOIData(
							KeyPhraseExtractor.getKeyPhraseSet(
									WebPageParser.parseWebPage(nextUrl)), strArray[1], strArray[2]);

					File file = new File(outFileName);
					FileWriter filewriter = null;
					if(file.exists()) {
						filewriter = new FileWriter(file, true);
					} else {
						filewriter = new FileWriter(file);
					}
					for(String record : spotSet) {
						//System.out.println(record + LocalSearcher.SPLITTER + nextUrl+ LocalSearcher.SPLITTER + Integer.toString(hatebuCount));
						filewriter.write(record + LocalSearcher.SPLITTER 
								+ nextUrl+ LocalSearcher.SPLITTER 
								+ Integer.toString(hatebuDto.getCount()) + LocalSearcher.SPLITTER
								+ hatebuDto.getTitle());
						filewriter.write("\n");
					}
					filewriter.close();
				}
			}
			
			System.out.println("end!!");
		} catch (Exception e) { 
			System.out.println(e.getMessage()); 
		} finally { 
			try { 
				if(bufferReader != null){ 
					bufferReader.close(); 
				} 
			} catch(Exception e) { 
			} 
		}


	}

}
