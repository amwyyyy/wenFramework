package com.wen.framework.controller;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.WebUtils;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.wen.framework.common.PageParams;
import com.wen.framework.common.ResultObject;
import com.wen.framework.domain.BaseEntity;
import com.wen.framework.service.BaseService;

/**
 * 基础controller类，controller都要继承此类
 * 
 * @author huangwg
 */
public abstract class BaseController<E extends BaseEntity, Id extends Serializable> {
	protected Logger log = Logger.getLogger(this.getClass());

	public static final String PARAM_PAGE_NUM = "pageNum";
	public static final String PARAM_PAGE_SIZE = "pageSize";
	public static final String PARAM_SORT_NAME = "sortName";
	public static final String PARAM_SORT_TYPE = "sortType";

	protected abstract BaseService<E, Id> getDefaultService();

	protected PageParams getPageParams(HttpServletRequest request, E dto) {
		Map<String, Object> params = WebUtils.getParametersStartingWith(request, "");
		PageParams page = new PageParams();
		if (params != null) {
			String pageNum = String.valueOf(params.get(PARAM_PAGE_NUM));
			if (StringUtils.isNumeric(pageNum)) {
				page.setPageNum(Integer.parseInt(pageNum));
				page.setEnabled(true);
			}

			String pageSize = String.valueOf(params.get(PARAM_PAGE_SIZE));
			if (StringUtils.isNumeric(pageSize)) {
				page.setPageSize(Integer.parseInt(pageSize));
				page.setEnabled(true);
			}

			String sortType = String.valueOf(params.get(PARAM_SORT_TYPE));
			if (StringUtils.isNotEmpty(sortType)) {
				page.setSortType(sortType);
			}

			String sortName = String.valueOf(params.get(PARAM_SORT_NAME));
			if (StringUtils.isNotEmpty(sortName)) {
				page.setSortName(sortName);
			}
		}
		return page;
	}

	@RequestMapping(value = "list.json", method = RequestMethod.POST)
	@ResponseBody
	protected ResultObject listJson(HttpServletRequest request, @ModelAttribute E dto) {
		ResultObject result = new ResultObject();
		try {
			PageParams pp = getPageParams(request, dto);
			if (pp.isEnabled()) {
				Page<E> page = getDefaultService().findByCondition(dto, pp.getPageNum(), pp.getPageSize(),
						pp.getSortName(), pp.getSortType());
				PageInfo<E> pageInfo = new PageInfo<E>(page);
				result.setData(pageInfo);
			} else {
				List<E> list = getDefaultService().findByCondition(dto);
				result.setData(list);
			}
		} catch (Exception e) {
			log.error("", e);
			result.setErrCode(ResultObject.ERRCODE_EXCEPTION);
			result.setMsg(e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/delete.json", method = RequestMethod.POST)
	@ResponseBody
	protected ResultObject deleteJson(@RequestParam(value = "id") Integer[] ids) {
		ResultObject result = new ResultObject();
		try {
			int n = 0;
			for (Integer id : ids) {
				n += getDefaultService().deleteById((Id) id);
			}
			result.setData(n);
		} catch (Exception e) {
			log.error("", e);
			result.setErrCode(ResultObject.ERRCODE_EXCEPTION);
			result.setMsg(e.getMessage());
		}
		return result;
	}

	@RequestMapping(value = "/view.json")
	@ResponseBody
	protected ResultObject viewJson(@RequestParam Id id) {
		ResultObject result = new ResultObject();
		try {
			E dto = getDefaultService().findById(id);
			result.setData(dto);
		} catch (Exception e) {
			log.error("", e);
			result.setErrCode(ResultObject.ERRCODE_EXCEPTION);
			result.setMsg(e.getMessage());
		}
		return result;
	}

//	@RequestMapping(value = "/save.json")
//	@ResponseBody
//	protected ResultObject save(@ModelAttribute E item, Id id) {
//		ResultObject result = new ResultObject();
//		try {
//			if (item == null) {
//				result.setErrCode(ResultObject.ERRCODE_EXCEPTION);
//				result.setMsg("对象为空");
//			} else if (id != null) {
//				getDefaultService().setTimeAndUser(item, "admin");
//				getDefaultService().updateById(item);
//			} else {
//				// 会自动设置主键
//				getDefaultService().setTimeAndUser(item, "admin");
//				getDefaultService().insert(item);
//			}
//
//			// 重新查询以便获取关联表信息
//			item = getDefaultService().findByEntityId(item);
//			result.setData(item);
//		} catch (Exception e) {
//			log.error("", e);
//			result.setErrCode(ResultObject.ERRCODE_EXCEPTION);
//			result.setMsg(e.getMessage());
//		}
//		return result;
//	}
}
