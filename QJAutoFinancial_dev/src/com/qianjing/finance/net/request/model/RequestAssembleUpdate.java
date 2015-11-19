package com.qianjing.finance.net.request.model;

import com.qianjing.finance.net.connection.UrlConst;


/**
 * Title: RequestAssembleUpdate
 * Description: 组合更新请求
 */
public class RequestAssembleUpdate extends RequestBase {

	/** 组合ID */
	private final String PARAM_SID = "sid";
	/** 组合类型 */
	private final String PARAM_TYPE = "type";
	/** 组合名称 */
	private final String PARAM_NAME = "nm";
	/** 初始投资金额 */
	private final String PARAM_INIT = "init";
	/** 期望收益率 */
	private final String PARAM_RATE = "rate";
	/** 风险偏好 */
	private final String PARAM_RISK = "risk";
	/** 年龄 */
	private final String PARAM_CURRENT_AGE = "age";
	/** 退休年龄 */
	private final String PARAM_RETIRE_AGE = "retire";
	/** 月定投金额 */
	private final String PARAM_MONTH = "month";
	/** 目标金额 */
	private final String PARAM_MONEY = "money";
	/** 投资年数 */
	private final String PARAM_YEAR = "year";
	
	private String strSid;
	private int type;
	private String strName;
	private String strInitSum;
	private int rate;
	private int risk;
	private int currentAge;
	private int retireAge;
	private String strMonthAmount;
	private String strGoalSum;
	private int years;
	
	public RequestAssembleUpdate(String strSid, int type, String strName, String strInitSum, int rate, 
			int risk, int currentAge, int retireAge, String strMonthAmount, String strGoalSum, int years){
		this.strSid = strSid;
		this.type = type;
		this.strName = strName;
		this.strInitSum = strInitSum;
		this.rate = rate;
		this.risk = risk;
		this.currentAge = currentAge;
		this.retireAge = retireAge;
		this.strMonthAmount = strMonthAmount;
		this.strGoalSum = strGoalSum;
		this.years = years;
		
		create();
	}
	
	/**
	 * 初始化请求
	 * @return
	 */
	public void create(){
		url = UrlConst.UPDATE_ASSEMBLE;
		
		properties.put(PARAM_SID, strSid);
		properties.put(PARAM_TYPE, type);
		properties.put(PARAM_NAME, strName);
		properties.put(PARAM_INIT, strInitSum);
		properties.put(PARAM_RATE, rate);
		properties.put(PARAM_RISK, risk);
		properties.put(PARAM_CURRENT_AGE, currentAge);
		properties.put(PARAM_RETIRE_AGE, retireAge);
		properties.put(PARAM_MONTH, strMonthAmount);
		properties.put(PARAM_MONEY, strGoalSum);
		properties.put(PARAM_YEAR, years);
	}
	
}
