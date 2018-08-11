/**
 * 
 */
package com.sdust.im.utils;

import java.util.Objects;

import com.pingan.pama.fileservice.FileUpload;
import com.pingan.pama.test.invoke.productCenter.IdentityAuthentication;
import com.sdust.im.bean.User;

/**
 * @author Andong
 *
 */
public final class RegisterUtils {
	
	/**
	 * 上传图片，进行身份验证
	 * @param user
	 * @throws Exception
	 */
	public static void RegisterMicroInnovationAPI(User user)throws Exception{
		Objects.requireNonNull(user,"传入user对象为空");
		Objects.requireNonNull(user.getPhoto(),"自拍照为空");
		Objects.requireNonNull(user.getIdentityNo(),"身份证为空");
		
		//1.上传图片文件名采用身份证号加毫秒值，保证不重复
		String fileId=FileUpload.uploadFile(user.getPhoto(), user.getIdentityNo()+System.currentTimeMillis());
		
		//补充userID
		user.setFileId(fileId);
		
		//2.库照比对，验证身份
		IdentityAuthentication iauth=new IdentityAuthentication();
		
		String channelBizNo=iauth.IdentityAuth(user.getUserName(),user.getIdentityNo(),fileId);
		
		String authResult=IdentityAuthentication.authQueryFinalResult(iauth, channelBizNo);
		
		if(AuthResultEnum.ONE.getValue().equals(authResult)){
			System.out.println("三项身份验证通过!!");
		}else{
			System.out.println("三项身份验证不通过!!开启两项验证!");
			//开启两项验证，如不通过则终止注册
			channelBizNo=iauth.IdentityAuthByIdentityNoAndUserName(user.getUserName(),user.getIdentityNo());
			
			authResult=IdentityAuthentication.authQueryFinalResult(iauth, channelBizNo);
			
			if(!AuthResultEnum.ONE.getValue().equals(authResult))
				throw new Exception("两项身份验证不通过!!");
		}
	}
}
