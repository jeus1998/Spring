package spring.upload.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriUtils;
import spring.upload.domain.Item;
import spring.upload.domain.ItemRepository;
import spring.upload.domain.UploadFile;
import spring.upload.file.FileStore;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ItemController {
    private final FileStore fileStore;
    private final ItemRepository itemRepository;
    @GetMapping("/items/new")
    public String itemAddForm(@ModelAttribute ItemForm form){
        return "item-form";
    }

    @PostMapping("/items/new")
    public String itemAdd(@ModelAttribute ItemForm form, RedirectAttributes redirectAttributes) throws IOException {

        UploadFile attachFile = fileStore.storeFile(form.getAttachFile());
        List<UploadFile> imageFiles = fileStore.storeFiles(form.getImageFiles());

        // 데이터 베이스 저장
        Item item = new Item();
        item.setItemName(form.getItemName());
        item.setAttachFile(attachFile);
        item.setImageFiles(imageFiles);

        Item saveItem = itemRepository.save(item);

        redirectAttributes.addAttribute("itemId", saveItem.getId());
        return "redirect:/items/{itemId}";
    }

    @GetMapping("/items/{itemId}")
    public String items(@PathVariable Long itemId, Model model){

        Item item = itemRepository.findById(itemId);
        model.addAttribute("item", item);

        return "item-view";
    }

    // 이미지 보여주기
    @ResponseBody
    @GetMapping("/images/{storeName}")
    public Resource downloadImage(@PathVariable String storeName) throws MalformedURLException {
        return new UrlResource("file:" + fileStore.getFullPath(storeName));
    }

    // 첨부파일 다운
    @GetMapping("/attach/{itemId}")
    public ResponseEntity<Resource> downloadAttach(@PathVariable Long itemId) throws MalformedURLException{

        Item item = itemRepository.findById(itemId);
        String uploadFileName = item.getAttachFile().getUploadFileName();
        String storeFileName = item.getAttachFile().getStoreFileName();

        // 파일 저장 경로
        UrlResource urlResource = new UrlResource("file:" + fileStore.getFullPath(storeFileName));

        // Upload 이름 인코딩
        String encodeUploadFileName = UriUtils.encode(uploadFileName, StandardCharsets.UTF_8);

        // Header 설정
        String contentDisposition = "attachment; filename=\"" + encodeUploadFileName + "\"";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);
    }
}
