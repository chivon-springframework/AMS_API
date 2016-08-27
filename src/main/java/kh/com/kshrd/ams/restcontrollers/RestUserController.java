package kh.com.kshrd.ams.restcontrollers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.mangofactory.swagger.annotations.ApiIgnore;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiImplicitParam;
import com.wordnik.swagger.annotations.ApiImplicitParams;
import com.wordnik.swagger.annotations.ApiOperation;

import kh.com.kshrd.ams.forms.UserForm;
import kh.com.kshrd.ams.models.ResponseList;
import kh.com.kshrd.ams.models.ResponseRecord;
import kh.com.kshrd.ams.models.User;
import kh.com.kshrd.ams.models.WishList;
import kh.com.kshrd.ams.services.UploadService;
import kh.com.kshrd.ams.services.UserService;
import kh.com.kshrd.ams.services.WishListService;
import kh.com.kshrd.ams.utilities.Pagination;

@RestController
@Api("USER MANAGEMENT API")
public class RestUserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UploadService uploadService;
	
	@Autowired
	private WishListService wishListService;
	
	@ApiOperation("TODO: TO SIGN UP A NEW USER")
	@RequestMapping(value="/v1/api/users", method = RequestMethod.POST)
	public ResponseRecord<User> signup(
			@RequestParam("EMAIL") String email,
			@RequestParam("NAME") String name,
			@RequestParam("PASSWORD") String password,
			@RequestParam("GENDER") String gender,
			@RequestParam("TELEPHONE") String telephone,
			@RequestParam("FACEBOOK_ID") String facebookId,
			@RequestParam("PHOTO") CommonsMultipartFile file, 
			HttpServletRequest request){
		ResponseRecord<User> responseModel = new ResponseRecord<User>();
		try {
			User user = new User();
			user.setEmail(email);
			user.setName(name);
			user.setPassword(password);
			user.setFacebookId(facebookId);
			user.setTelephone(telephone);
			user.setGender(gender);
			user.setImageUrl(uploadService.uploadMultipart(file, request));
			
			user = userService.signUp(user);
			if(user!=null){
				responseModel.setCode("0000");
				responseModel.setMessage("YOU HAVE BEEN SIGNED UP SUCCESSFULLY.");
				responseModel.setData(user);
			}else{
				responseModel.setCode("9999");
				responseModel.setMessage("YOU HAVE BEEN SIGNED UP FAILURE.");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return responseModel;
	}
	
	@ApiOperation("TODO: TO SIGN IN A NEW USER")
	@RequestMapping(value="/v1/api/authentication", method = RequestMethod.POST)
	public ResponseRecord<User> signIn(@RequestBody UserForm.SignInForm form){
		ResponseRecord<User> response = new ResponseRecord<User>();
		try {
			User user = new User();
			user.setEmail(form.getEmail());
			user.setPassword(form.getPassword());
			User userLogined = userService.signIn(user);
			if(userLogined==null){
				response.setCode("9999");
				response.setMessage("YOU HAVE BEEN FIALED WHEN LOGIN PLEASE TRY AGAIN.");
			}else{
				response.setCode("0000");
				response.setMessage("YOU HAVE BEEN SIGNED IN SUCCESSFULLY.");
				response.setData(userLogined);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return response;
	}
	
	@ApiOperation("TODO: TO LIST ALL USERS")
	@RequestMapping(value="/v1/api/users", method = RequestMethod.GET)
	public ResponseList<User> getAllUsers(){
		ResponseList<User> response = new ResponseList<User>();
		try{
			response.setCode("0000");
			response.setData(userService.findAllUsers());
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return response;
		
	}
	
	@ApiOperation("TODO: TO LIST ALL WISH LIST BY USER ID")
	@ApiImplicitParams({
	    @ApiImplicitParam(name = "page", dataType = "integer", paramType = "query", defaultValue="1",
	            value = "Results page you want to retrieve (1..N)"),
	    @ApiImplicitParam(name = "limit", dataType = "integer", paramType = "query", defaultValue="15",
	            value = "Number of records per page."),
	})
	@RequestMapping(value="/v1/api/users/{userId}/wishlists", method = RequestMethod.GET)
	public ResponseList<WishList> findAllWishListByUserId(@PathVariable("userId") Long userId, @ApiIgnore Pagination pagination){
		ResponseList<WishList> response = new ResponseList<WishList>();
		try{
			response.setCode("0000");
			response.setData(wishListService.findAllWishLists(userId, pagination));
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return response;
	}

}
