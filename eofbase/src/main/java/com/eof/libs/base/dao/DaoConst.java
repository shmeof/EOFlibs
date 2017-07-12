package com.eof.libs.base.dao;

/**
 * 存储常量
 * 
 * @author danyangguo
 *
 */
public class DaoConst {
	public static class SpName {
		public final static String SP_GUID = "guid"; // 设备注册信息
		public final static String SP_USID = "usid"; // 账号信息
		public final static String SP_CONF = "conf"; // 配置信息
		public final static String SP_FREQ = "freq"; // 频率控制文件
	}
	
	public static class CONF {
		public final static String SERVER_TYPE = "st"; // 服务器类型 
		public final static String EVER_DO_SHORTCUT = "sc"; // 曾经创建过快捷方式
		public final static String GUARD_VERSION = "gv"; // 引导图版本，如果和本地保存的不一样，会重新展现引导图
	}

	public static class USID {
		public final static String IS_NEW_USER = "in"; // 是否是全新新用户
		public final static String USER_ID = "id"; // 用户账号id，可唯一标识用户
		public final static String USER_NAME = "un"; // 用户账号名称，可唯一标识用户
		public final static String SEX_TYPE = "st"; // 性别
		public final static String NICK_NAME = "nn"; // 昵称
		public final static String AVATAR_ID = "atid"; // 头像唯一标识id
		public final static String AVATAR_RELATIVEURL = "atrurl"; // 头像资源相对路径
		public final static String AVATAR_ABSOLUTEURL = "ataurl"; // 头像资源绝对路径
		public final static String USER_INFO_BASE64 = "usinfo"; // 用户信息
	}
	
	public static class GUID {
		public final static String GUID_UUID = "ud"; 
		public final static String GUID_GUID = "gd"; 
	}
	
	public static class FREQ {
		public final static String IS_FIRST_RUN_APP = "ifapp";  // 是否是首次运行app
		public final static String IS_FIRST_FILE_CACHE = "ifr";  // 是否是首次图片文件缓存
		public final static String FREQUENCE_1HOUR = "1h"; // 频率1小时
		public final static String FREQUENCE_1DAY = "1d"; // 频率1天
		public final static String FREQUENCE_3DAY = "3d"; // 频率3天
		public final static String FREQUENCE_7DAY = "7d"; // 频率7天
		public final static String FREQUENCE_1MONTH = "1m"; // 频率1个月
		public final static String FREQUENCE_3MONTH = "3m";// 频率3个月
		
		public final static String UPDATE_KTVINFO_TIMESTAMP = "kinfo"; // 最后一次更新KtvInfos的时间点

		public final static String UPDATE_APP_NEW_VERSION = "nv"; // 最新版本的版本号
		public final static String UPDATE_APP_NEW_BUILDNO = "nb"; // 最新版本的buildno
		public final static String UPDATE_APP_NEW_TITLE = "ntl"; // 最新版本的提示标题 
		public final static String UPDATE_APP_NEW_MSG = "nmsg"; // 最新版本的提示内容
		public final static String UPDATE_APP_NEW_URL = "nurl"; // 最新版本链接
		public final static String UPDATE_APP_NEW_URLTYPE = "nurltp"; // 最新版本链接类型
		
		public final static String TIMESTAMP_LAST_POKE = "tmltpoke"; // 上一次poke的时间戳 
	}
	
	public static class MSG {
		public final static String STUFF = "st"; // 消息的stuff
	}

	public static class NOTE {
		public final static String TIMESTAMP = "ts";  // 手记统计更新时间戳
		public final static String TOTAL_COUNT = "tl_ct";  // 手记总共可上传条数
		public final static String TOTAL_SIZE = "tl_sz";  // 手记空间总大小
		public final static String USED_COUNT = "usd_ct";  // 手记已上传条数
		public final static String USED_SIZE = "usd_sz";  // 手记已使用空间大小
		public final static String TEXT_COUNT = "txt_ct";  // 文本
		public final static String TEXT_SIZE = "txt_sz";  // 文本
		public final static String IMAGE_COUNT = "img_ct";  // 图片
		public final static String IMAGE_SIZE = "img_sz";  // 图片
		public final static String AUDIO_COUNT = "ado_ct";  // 音频
		public final static String AUDIO_SIZE = "ado_sz";  // 音频
		public final static String VIDIO_COUNT = "vdo_ct";  // 视频
		public final static String VIDIO_SIZE = "vdo_sz";  // 视频
		public final static String NOTE_USED_COUNT = "nt_usd_ct";  // 手记条数 
		public final static String NOTE_USED_SIZE = "nt_usd_sz";  // 手记空间
		public final static String IMAGETONGUE_COUNT = "img_tg_ct";  // 图片：舌象 
		public final static String IMAGETONGUE_SIZE = "img_tg_sz";  // 图片：舌象
		public final static String IMAGEFACE_COUNT = "img_fc_ct";  // 图片：面相
		public final static String IMAGEFACE_SIZE = "img_fc_sz";  // 图片：面相
		public final static String IMAGERECIPEL_COUNT = "img_re_ct";  // 图片：处方
		public final static String IMAGERECIPEL_SIZE = "img_re_sz";  // 图片：处方
		public final static String IMAGEOTHER_COUNT = "img_ot_ct";  // 图片：其他
		public final static String IMAGEOTHER_SIZE = "img_ot_sz";  // 图片：其他
		public final static String EVER_SHOW_NOTE_LIST = "ev_sh_nt_lt";  // 此次登录是否已经展现过手记恢复过程
		
	}

	public static class TOPIC {
		public final static String DRAFT_HAVE = "tp_draft_have";  // 是否有帖子草稿
		public final static String DRAFT_BODY = "tp_draft_body";  // 帖子草稿内容
		
	}
}
