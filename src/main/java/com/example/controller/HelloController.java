package com.example.controller;

import java.io.IOException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.example.excel.DataToExcel;
import com.example.model.UserModel;
import com.example.service.UserService;

@Controller
public class HelloController {
	
	@Autowired
	private UserService service;
	
	
//	Main path
	@GetMapping("/")
	public String getTemplate(Model model) {
		UserModel user = new UserModel();
		model.addAttribute("user",user);
		return "index";
	}
	
	@GetMapping(path="/user")
	public @ResponseBody List<UserModel> getAllUser(){
		return service.getAllUsers(); //Return data in JSON format
	}
	
//	View user
	@RequestMapping(value="/view/user")
    public String ViewUsers(Model model) {

        List<UserModel> users = service.getAllUsers();

        model.addAttribute("users", users);

        return "show";
    }
	
//	Add new user to the database
	@RequestMapping(value="/saveUser")
    public String saveUser(@Valid @ModelAttribute("user") UserModel user,BindingResult result) throws Exception{
        // save user to database
		if(result.hasErrors()) {
			return "index";
		}
        service.saveUser(user);
        return "redirect:/view/user";
    }
	
//	Update existing user into database
	@RequestMapping(value="/edit/{id}")
	public ModelAndView editUser(@PathVariable(name = "id") int id) {
		ModelAndView mav = new ModelAndView("edit");
		UserModel user = service.getUserId(id);
		mav.addObject("user",user);
		
		return mav;
		
	}

//	Delete User
	@RequestMapping(value="/delete/{id}")
	public String deleteuser(@PathVariable(name = "id") int id) {
		service.delete(id);
		return "redirect:/view/user";
	}

//	Read from excel
	@RequestMapping(value="/import")
	 public String mapReapExcelDatatoDB(@RequestParam("file") MultipartFile reapExcelDataFile, Model model) throws IOException {

	       List<UserModel> userList = new ArrayList<UserModel>();
	        XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
	        XSSFSheet worksheet = workbook.getSheetAt(0);

	        for(int i=1;i<worksheet.getPhysicalNumberOfRows() ;i++) {
	            UserModel tempUser = new UserModel();

	            XSSFRow row = worksheet.getRow(i);
	            DataFormatter formatter = new DataFormatter();
	            
	            tempUser.setId((int) row.getCell(0).getNumericCellValue());
	            tempUser.setName(row.getCell(1).getStringCellValue());
	            tempUser.setPhno(formatter.formatCellValue(row.getCell(2)));
	            tempUser.setEmail(row.getCell(3).getStringCellValue());
	            userList.add(tempUser);  
	            
	            model.addAttribute("userList",userList);
	        }
	        
	        return "Excel";
	    }	
	
	
	
	//Export to excel
	@GetMapping("/users/export/excel")
    public void exportToExcel(HttpServletResponse response) throws IOException {
        response.setContentType("application/octet-stream");
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
        String currentDateTime = dateFormatter.format(new Date(0));
         
        String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
        response.setHeader(headerKey, headerValue);
         
        List<UserModel> listUsers = service.getAllUsers();
         
        DataToExcel excelExporter = new DataToExcel(listUsers);
         
        excelExporter.export(response);    
    }  
}
