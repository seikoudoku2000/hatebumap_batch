package com.seikoudoku2000.hatebumap.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import com.seikoudoku2000.hatebumap.batch.player.HatebuInfoGetter;
import com.seikoudoku2000.hatebumap.batch.player.LocalSearcher;
import com.seikoudoku2000.hatebumap.batch.player.HatebuInfoGetter.HatebuInfoDto;

/**
 * DBに投入するためのCSVを生成するためのバッチ処理。
 * POI検索ワードの手動抽出版。
 * @author tomitayousuke
 *
 */
public class CreateDataSemiManual {
	
	public static void main(String[] args) throws Exception {
		
		//このフォルダ配下に解析させたいファイル群を配置しておく。
		//String bathDirPath = "/Users/tomitayousuke/eclipse/workspace/hatebumap/sample/test";
		String bathDirPath = "/Users/tomitayousuke/eclipse/workspace/hatebumap/sample/1108final";
		//結果出力用のファイル名
		String outFileName = bathDirPath + "/" + new SimpleDateFormat("MMddHHmm").format(new Date()) + ".csv";
		//結果書き込み用オブジェクト
		FileWriter fileWriter = new FileWriter(outFileName, true);
		
		//配下のファイル取得
		File bathDir = new File(bathDirPath);
		File [] files = bathDir.listFiles();
	
		//ファイルのリストでループ
		for(File nextFile : files) {
			if(nextFile.isFile()){
				BufferedReader bufferReader = null; 
				String str = null;
				HatebuInfoDto hatebuDto = null;
				//ローカルサーチの住所コード
				String acStr = null;
				//ローカルサーチの業種コード
				String gcStr = null;
				boolean isFirstLine = true;
				bufferReader = new BufferedReader(
						new InputStreamReader(
								new FileInputStream(nextFile),"UTF-8")); 
				//一行ずつ読み込み
				while ((str = bufferReader.readLine()) != null) { 
					//１行目は検索の基本データ
					if(isFirstLine) {
						isFirstLine = false;
						String[] strArray = str.split("\t");
						//はてぶ情報の取得
						hatebuDto = HatebuInfoGetter.getHatebuInfoLite(strArray[0]);
						if(strArray.length >= 2) {
							acStr = strArray[1];
						}
						if(strArray.length >= 3) {
							gcStr = strArray[2];
						}
					//２行目以降はローカルサーチの検索語	
					} else {
						Set<String> resultSet
							= LocalSearcher.getPOIData(str, acStr, gcStr);
						for(String resultStr : resultSet) {
							StringBuilder sb = new StringBuilder();
							sb.append(resultStr);
							sb.append(LocalSearcher.SPLITTER + hatebuDto.getUrl());
							sb.append(LocalSearcher.SPLITTER + hatebuDto.getCount());
							sb.append(LocalSearcher.SPLITTER + hatebuDto.getTitle().replace(",", " "));
							sb.append("\n");
							System.out.print(sb.toString());
							fileWriter.write(sb.toString());
						}
					}
				}
			}
		}
		fileWriter.close();
	}
}
