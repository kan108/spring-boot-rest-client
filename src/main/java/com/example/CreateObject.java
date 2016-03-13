package com.example;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.test.dto.AppsInpt;
import com.example.test.dto.HatApps;
import com.example.test.dto.RowData;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateObject {

	private static final String FILE_NAME = "data.xlsx";
	private static final String SHEET_NAME = "Sheet1";

	private static final String JSON_FILE_NAME = "testdata.json";

	private static final int HAT_APPS_ROW_START = 3;
	private static final int HAT_APPS_COLUMN_START = 2;

	public static void main(String[] args) throws Exception {

		
		// Excelからデータを読み込みオブジェクトを作成する
		
		// Jsonを作成する
		String json = createJson();

		// Json文字列をファイルに出力する。
		outputFile(json);
		
		// Jsonファイルからデータを読み込む
		ObjectMapper mapper = new ObjectMapper();
		File file = new File(JSON_FILE_NAME);
		HatApps hatApps = mapper.readValue(file, HatApps.class);
		
		System.out.println(hatApps);

	};

	private static void outputFile(String json) throws Exception {
		// ファイルに出力する
		File file = new File(JSON_FILE_NAME);
		PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		pw.println(json);
		pw.flush();
		pw.close();

	}

	private static String createJson() throws Exception {
		CreateObject createObject = new CreateObject();

		// AppsInpt
		List<RowData> listAppsInpt = createObject.readExcel(2, 20, 5);
		AppsInpt appsInpt = createObject.createObjectAppsInpt(listAppsInpt);

		// HatApps
		List<RowData> listHatApps = createObject.readExcel(2, 20, 1);
		HatApps hatApps = createObject.createObjectHatApps(listHatApps, appsInpt);

		System.out.println(hatApps);

		// オブジェクトをjsonに変換する
		ObjectMapper mapper = new ObjectMapper();
		String json = mapper.writeValueAsString(hatApps);
		System.out.println("jsonデータ：" + json);

		return json;

	}

	private HatApps createObjectHatApps(List<RowData> dataList, AppsInpt appsInpt) throws Exception {

		HatApps hatApps = new HatApps();

		hatApps.setAppsInpt(appsInpt);

		Class clsHatApps = hatApps.getClass();

		for (RowData data : dataList) {

			// String
			if ("String".equals(data.getKata())) {
				Method m1 = clsHatApps.getMethod("set" + firstCharConvUpper(data.getFieldName()), String.class);
				m1.invoke(hatApps, data.getValue());
			}

			// List<String>
			if ("List<String".equals(data.getKata())) {
				Method m2 = clsHatApps.getMethod("set" + firstCharConvUpper(data.getFieldName()), List.class);
				List<String> l = new ArrayList<>();
				String[] ss = data.getValue().split(",");
				for (String s : ss) {
					l.add(s);
				}
				m2.invoke(hatApps, l);

			}

			// Date
			if ("Date".equals(data.getKata())) {
				Method m3 = clsHatApps.getMethod("set" + firstCharConvUpper(data.getFieldName()), Date.class);
				m3.invoke(hatApps, new Date(data.getValue()));
			}
		}

		return hatApps;

	}

	private AppsInpt createObjectAppsInpt(List<RowData> dataList) throws Exception {

		AppsInpt appsInpt = new AppsInpt();

		Class clsAppsInpt = appsInpt.getClass();

		for (RowData data : dataList) {

			// �@String
			if ("String".equals(data.getKata())) {
				Method m1 = clsAppsInpt.getMethod("set" + firstCharConvUpper(data.getFieldName()), String.class);
				m1.invoke(appsInpt, data.getValue());
			}

			// List<String>
			if ("List<String".equals(data.getKata())) {
				Method m2 = clsAppsInpt.getMethod("set" + firstCharConvUpper(data.getFieldName()), List.class);
				List<String> l = new ArrayList<>();
				String[] ss = data.getValue().split(",");
				for (String s : ss) {
					l.add(s);
				}
				m2.invoke(appsInpt, l);

			}
		}

		return appsInpt;

	}

	private List<RowData> readExcel(int rowStart, int rowEnd, int columnStart) {

		List<RowData> dataList = new ArrayList<>();

		try {
			FileInputStream fi = new FileInputStream(FILE_NAME);
			Workbook book = new XSSFWorkbook(fi);
			fi.close();
			for (int s = 0; s < book.getNumberOfSheets(); ++s) {
				Sheet sheet = book.getSheetAt(s);
				if (SHEET_NAME.equals(sheet)) {
					continue;
				}

				for (int i = rowStart; i < rowEnd; i++) {

					Row row = sheet.getRow(i);

					if (row == null) {
						break;
					}

					RowData data = new RowData();
					int a = columnStart;
					data.setKata(getStr(row.getCell(a)));
					data.setFieldName(getStr(row.getCell(++a)));
					data.setValue(getStr(row.getCell(++a)));

					dataList.add(data);

				}

			}
		} catch (Exception e) {
			e.printStackTrace(System.err);

		}

		for (RowData row : dataList) {
			System.out.println(row.toString());
		}

		return dataList;

	}

	public static String getStr(Cell cell) { // �f�[�^�^���̓ǂݎ��
		if (cell == null) {
			return "";
		}

		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_BOOLEAN:
			return Boolean.toString(cell.getBooleanCellValue());
		case Cell.CELL_TYPE_FORMULA:
			return cell.getCellFormula();
		case Cell.CELL_TYPE_NUMERIC:
			return Double.toString(cell.getNumericCellValue());
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		}
		return "";// CELL_TYPE_BLANK,CELL_TYPE_ERROR
	}

	/**
	 * 先頭文字を大文字に変更
	 * 
	 * @param s
	 * @return
	 */
	public String firstCharConvUpper(String s) {

		if (s == null) {
			return null;
		}

		return s.substring(0, 1).toUpperCase() + s.substring(1);
	}

}
