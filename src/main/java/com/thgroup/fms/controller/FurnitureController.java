package com.thgroup.fms.controller;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.thgroup.fms.entity.Furniture;
import com.thgroup.fms.entity.Promotion;
import com.thgroup.fms.entity.Category;
import com.thgroup.fms.service.CategoryService;
import com.thgroup.fms.service.FurnitureService;
import com.thgroup.fms.service.PromotionService;
import com.thgroup.fms.utils.Helper;

@Controller
public class FurnitureController {
	@Autowired
	private FurnitureService furnitureService;
	@Autowired
	private PromotionService promotionService;
	@Autowired
	private CategoryService categoryService;
	
	public FurnitureController(FurnitureService furnitureService, PromotionService promotionService,
			CategoryService categoryService) {
		super();
		this.furnitureService = furnitureService;
		this.promotionService = promotionService;
		this.categoryService = categoryService;
	}

	@GetMapping("/admin/furniture")
	public String furnituresListPage(Model model) {
		model.addAttribute("furnituresList", this.furnitureService.getAllFurnitures());
		return "admin/furniture/index";
	}
	
	@GetMapping("/admin/detail-furniture/{idNoiThat}")
	public String furnitureDetailPage(@PathVariable(value="idNoiThat") int idNoiThat, Model model) {
		Furniture furniture = this.furnitureService.getFurnitureById(idNoiThat);
		model.addAttribute("furniture", furniture);
		return "admin/furniture/detail";
	}
	    
	@GetMapping("/admin/create-furniture")
	public String createFurniturePage(Model model) {
	    Furniture furniture = new Furniture();
	    List<Category> categories = this.categoryService.getAllCategories();
 	    List<Promotion> promotions = this.promotionService.getAllPromotions();

 	    String newId = Helper.getNewID(this.furnitureService.getMaxId(), 2, 2, "NT");
	    furniture.setMaNoiThat(newId);
 	    
	    model.addAttribute("promotionsList", promotions);
	    model.addAttribute("categoriesList", categories);
	    model.addAttribute("furniture", furniture);
	    return "admin/furniture/add_furniture";
	}
	    
	@PostMapping("/admin/save-furniture")
	public String saveFurniture(@ModelAttribute("furniture") Furniture furniture,
			@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttrs , @Param("img") String img){
		
		if (furniture.getMaNoiThat() == null) {
			String newId = Helper.getNewID(this.furnitureService.getMaxId(), 2, 2, "NT");
		    furniture.setMaNoiThat(newId);
		    furniture.setHinhAnh(file.getOriginalFilename());
		}
		
		else {
			if (file.isEmpty()) {
				furniture.setHinhAnh(img);
			}
			else
			{
				furniture.setHinhAnh(file.getOriginalFilename());
			}
		}
	
			
		Furniture isExists = this.furnitureService.saveFurniture(furniture);
		if (isExists != null) {
			Path path = Paths.get("src/main/resources/static/admin/img/");
			try {
				InputStream inputStream = file.getInputStream();
				Files.copy(inputStream, path.resolve(file.getOriginalFilename()),
						StandardCopyOption.REPLACE_EXISTING);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		} 
		redirectAttrs.addFlashAttribute("alertType", "success");
		redirectAttrs.addFlashAttribute("alertText", "Thành công");
		return "redirect:/admin/furniture";
	}
	
	@GetMapping("/admin/update-furniture/{idNoiThat}")
	public String updateFurniturePage(@PathVariable(value="idNoiThat") int idNoiThat, Model model) {
		Furniture furniture = this.furnitureService.getFurnitureById(idNoiThat);
		List<Category> categories = this.categoryService.getAllCategories();
		List<Promotion> promotions = this.promotionService.getAllPromotions();
		model.addAttribute("promotionsList", promotions);
	 	model.addAttribute("categoriesList", categories);
	    model.addAttribute("furniture", furniture);
	    return "admin/furniture/update_furniture";
	}
	
	@GetMapping("/admin/delete-furniture/{idNoiThat}")
	public String deleteFurniture(@PathVariable(value="idNoiThat") int idNoiThat, 
			RedirectAttributes redirectAttrs) {
		this.furnitureService.removeFurniture(idNoiThat);
		redirectAttrs.addFlashAttribute("alertType", "success");
		redirectAttrs.addFlashAttribute("alertText", "Xóa thành công");
	    return "redirect:/admin/furniture";
	}
	

}