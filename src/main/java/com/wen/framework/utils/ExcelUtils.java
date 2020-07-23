package com.wen.framework.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.wen.framework.common.BaseEO;
import com.wen.framework.common.annotation.ExcelFormat;
import com.wen.framework.common.annotation.RowHeight;
import com.wen.framework.common.annotation.TableColumn;
import com.wen.framework.common.annotation.TableHeader;
import com.wen.framework.common.annotation.TableHeaders;

/**
 * 导出通用Excel工具类
 * @author denis.huang
 * @date 2016年4月27日
 */
public class ExcelUtils {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat();
	
	/**
	 * 导出excel通用方法
	 */
	public static <E> ByteArrayOutputStream exportExcel(List<E> list, Class<? extends BaseEO<E>> clazz) throws IOException 
	{
		long start = System.currentTimeMillis();
		
		Workbook wb = getWorkbook(clazz);
		Sheet sheet = initSheet(wb, clazz);
		
		int rowNum = createTableHead(wb, sheet, clazz);
		createTableBody(wb, sheet, clazz, list, rowNum);
		
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		wb.write(output);
		
		System.out.println("导出Excel用时：" + (System.currentTimeMillis() - start));
		return output;
	}
	
	/**
	 * 获取03或07格式的workbook
	 */
	private static Workbook getWorkbook(Class<?> clazz) 
	{
		ExcelFormat excelFormat = clazz.getAnnotation(ExcelFormat.class);
		Workbook wb = null;
		if (excelFormat == null || excelFormat.value().equals("xls")) 
		{
			wb = new HSSFWorkbook();
		} 
		else 
		{
			wb = new XSSFWorkbook();
		}
		return wb;
	}
	
	/**
	 * 初始化sheet, 设置宽度
	 */
	private static Sheet initSheet(Workbook wb, Class<?> clazz) 
	{
		Sheet sheet = wb.createSheet("sheet1");
		Field[] fields = BeanUtils.findSuperFields(clazz);
		
		if (fields == null || fields.length == 0) 
		{
			throw new RuntimeException("对象没有可导出字段");
		}
		
		int colCount = 0;
		for (int n=0; n<fields.length; n++) 
		{
			TableColumn tc = fields[n].getAnnotation(TableColumn.class);
			if (tc != null) 
			{
				sheet.setColumnWidth(n, tc.width() * 256);
				colCount++;
			}
		}
		
		if (colCount == 0) 
		{
			throw new RuntimeException("对象没有可导出字段");
		}
		
		return sheet;
	}

	/**
	 * 生成表主体
	 * @param <E>
	 */
	private static <E> void createTableBody(Workbook wb, Sheet sheet, Class<?> clazz, List<E> list, int rowNum) 
	{
		if (list == null || list.size() == 0) 
		{
			return;
		}
		
		short rowHeight = getRowHeight(clazz);
		Field[] fields = BeanUtils.findSuperFields(clazz);
		
		Map<String, TableColumn> tableColumnMap = getTableColumnMap(fields);
		
		for (int n=0; n<list.size(); n++) 
		{
			if (list.get(n) == null) 
			{
				continue;
			}
			
			Row row = sheet.createRow(n + rowNum);
			row.setHeight((short)(rowHeight * 30));
			
			int cellNum = 0;
			for (Field field : fields) 
			{
				TableColumn tc = tableColumnMap.get(field.getName());
				if (tc != null) 
				{
					CellStyle style = createCellStyle(wb, tc);
					Cell cell = row.createCell(cellNum++);
					cell.setCellStyle(style);
					
					if (field.getType().equals(Date.class)) 
					{
						try 
						{
							Date date = (Date) field.get(list.get(n));
							cell.setCellValue(getDateValue(tc.format(), date));
						} 
						catch (IllegalAccessException | IllegalArgumentException e) 
						{
							cell.setCellValue("");
						}
					}
					else if (field.getType().equals(Integer.class))
					{
						try
						{
							Integer value = (Integer) field.get(list.get(n));
							cell.setCellValue(value);
						} catch (IllegalArgumentException | IllegalAccessException e)
						{
							cell.setCellValue("");
						}
					}
					else 
					{
						try 
						{
							String value = ConvertUtils.convert(field.get(list.get(n)));
							cell.setCellValue(value);
						} 
						catch (IllegalAccessException | IllegalArgumentException e) 
						{
							cell.setCellValue("");
						}
					}
				}
			}
		}
	}
	
	/**
	 * 将TableColumn注解放在map中加快读取速度 
	 */
	private static Map<String, TableColumn> getTableColumnMap(Field[] fields) {
		Map<String, TableColumn> tableColumnMap = new HashMap<>();
		for (Field field : fields)
		{
			tableColumnMap.put(field.getName(), field.getAnnotation(TableColumn.class));
		}
		return tableColumnMap;
	}

	/**
	 * 取行高
	 */
	private static <E> short getRowHeight(Class<?> clazz) 
	{
		RowHeight rh = clazz.getAnnotation(RowHeight.class);
		if (rh == null) 
		{
			return 12;
		}
		return rh.value();
	}

	/**
	 * 格式化日期类型
	 */
	private static String getDateValue(String format, Date date) 
	{
		sdf.applyPattern(format);
		return sdf.format(date);
	}

	/**
	 * 表主体样式 
	 */
	private static CellStyle createCellStyle(Workbook wb, TableColumn tc) 
	{
		CellStyle style = wb.createCellStyle();
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
		style.setAlignment(tc.alignment());					//水平对齐方式
		style.setFillForegroundColor(tc.color());			//背景色
		style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//背景色填充方式
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN); 	//下边框
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);		//左边框
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);		//上边框
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);	//右边框
		
		Font font = wb.createFont();     
        font.setFontHeightInPoints(tc.fontSize());   		//设置字体大小  
        font.setBoldweight(tc.bold());						//设置字体加粗
        font.setFontName(tc.fontName());				  	//设置字体  
        font.setUnderline(tc.underline());					//设置下划线样式
        font.setColor(tc.fontColor());						//设置字体颜色
        style.setFont(font);
		
		return style;
	}

	/**
	 * 生成表头
	 * @return 生成表头后行号
	 */
	private static int createTableHead(Workbook wb, Sheet sheet, Class<?> clazz) 
	{
		TableHeader[] thArray = getTableHeader(clazz);
		
		int rowNum = 0;
		for (TableHeader th : thArray) 
		{
			CellStyle headStyle = createHeadStyle(wb, th);
			Row row = sheet.createRow(rowNum);
			row.setHeight((short)(th.height() * 30));	//设置行高
			
			String[] headers = th.value().split(",");
			if (headers.length == 0) 
			{
				throw new RuntimeException("表头不能为空");
			}
			
			int cellNum = headers.length - 1;
			int colspan = 0;
			for (int n=headers.length-1; n>=0; n--)	//从最后一列开始处理，为了能方便处理#col 
			{	
				Cell cell = row.createCell(cellNum);
				cell.setCellStyle(headStyle);
				
				if (headers[n].equals("#col"))	//有可能会合并多个单元格，所以累计起来 
				{
					colspan++;
				} 
				else if (headers[n].equals("#row")) 
				{
					CellRangeAddress region = new CellRangeAddress(rowNum-1, rowNum, cellNum, cellNum);
					sheet.addMergedRegion(region);
				} 
				else 
				{
					if (colspan != 0) 
					{
						CellRangeAddress region = new CellRangeAddress(rowNum, rowNum, cellNum, cellNum + colspan);
						sheet.addMergedRegion(region);
						colspan = 0;
					}
					cell.setCellValue(headers[n]);
				}
				
				cellNum--;
			}
			
			cellNum = 0;
			rowNum++;
		}
		
		return rowNum;
	}
	
	/**
	 * 获取表头注解
	 */
	private static TableHeader[] getTableHeader(Class<?> clazz) 
	{
		TableHeaders ths = clazz.getAnnotation(TableHeaders.class);
		TableHeader[] thArray = null;
		
		if (ths != null) 
		{
			thArray = ths.value();
		} 
		else 
		{
			TableHeader th = clazz.getAnnotation(TableHeader.class);
			if (th != null) 
			{
				thArray = new TableHeader[1];
				thArray[0] = th;
			}
		}
		
		if (thArray ==null || thArray.length ==0) 
		{
			throw new RuntimeException("没有定义表头");
		}
		
		return thArray;
	}
	
	/**
	 * 创建表头样式
	 */
	private static CellStyle createHeadStyle(Workbook wb, TableHeader th) 
	{
		CellStyle style = wb.createCellStyle();
		style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);//垂直对齐方式
		style.setAlignment(th.alignment());					//水平对齐方式
        style.setFillForegroundColor(th.color());			//设置背景色
        style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);//背景色填充方式
        style.setBorderBottom(HSSFCellStyle.BORDER_THIN); 	//下边框
        style.setBorderLeft(HSSFCellStyle.BORDER_THIN);		//左边框
        style.setBorderTop(HSSFCellStyle.BORDER_THIN);		//上边框
        style.setBorderRight(HSSFCellStyle.BORDER_THIN);	//右边框
        
        Font font = wb.createFont();     
        font.setFontHeightInPoints(th.fontSize());   		//设置字体大小  
        font.setBoldweight(th.boldWeight());				//设置粗体
        font.setFontName(th.fontName());				  	//设置字体  
        style.setFont(font);
        
        return style;
	}
}
