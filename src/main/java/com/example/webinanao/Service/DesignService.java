package com.example.webinanao.Service;

import com.example.webinanao.Dto.request.DesignRequest;
import com.example.webinanao.Dto.response.DesignResponse;
import com.example.webinanao.Entity.Design;
import com.example.webinanao.Entity.Product;
import com.example.webinanao.Entity.User;
import com.example.webinanao.Repo.DesignRepository;
import com.example.webinanao.Repo.ProductRepository;
import com.example.webinanao.Repo.UserRepository;
import com.example.webinanao.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DesignService {

    private final DesignRepository designRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public List<DesignResponse> getPublicDesigns() {
        return designRepository.findByIsPublicTrueOrderByCreatedAtDesc()
                .stream()
                .map(this::toDesignResponse)
                .collect(Collectors.toList());
    }

    public List<DesignResponse> getDesignsByUserId(Integer userId) {
        return designRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toDesignResponse)
                .collect(Collectors.toList());
    }

    public DesignResponse getDesignById(Integer designId) {
        Design design = designRepository.findById(designId)
                .orElseThrow(() -> new ResourceNotFoundException("Thiết kế không tồn tại với id: " + designId));
        return toDesignResponse(design);
    }

    @Transactional
    public DesignResponse createDesign(DesignRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

        Design design = new Design();
        design.setUser(user);
        design.setProduct(product);
        design.setDesignName(request.getDesignName());
        design.setPreviewUrl(request.getPreviewUrl());
        design.setDesignData(request.getDesignData());
        design.setSize(request.getSize());
        design.setColor(request.getColor());
        design.setIsPublic(request.getIsPublic() != null ? request.getIsPublic() : false);

        return toDesignResponse(designRepository.save(design));
    }

    @Transactional
    public DesignResponse updateDesign(Integer designId, DesignRequest request) {
        Design design = designRepository.findById(designId)
                .orElseThrow(() -> new ResourceNotFoundException("Thiết kế không tồn tại với id: " + designId));

        if (request.getProductId() != null) {
            Product product = productRepository.findById(request.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));
            design.setProduct(product);
        }

        if (request.getDesignName() != null) design.setDesignName(request.getDesignName());
        if (request.getPreviewUrl() != null) design.setPreviewUrl(request.getPreviewUrl());
        if (request.getDesignData() != null) design.setDesignData(request.getDesignData());
        if (request.getSize() != null) design.setSize(request.getSize());
        if (request.getColor() != null) design.setColor(request.getColor());
        if (request.getIsPublic() != null) design.setIsPublic(request.getIsPublic());

        return toDesignResponse(designRepository.save(design));
    }

    @Transactional
    public void deleteDesign(Integer designId) {
        Design design = designRepository.findById(designId)
                .orElseThrow(() -> new ResourceNotFoundException("Thiết kế không tồn tại với id: " + designId));
        designRepository.delete(design);
    }

    private DesignResponse toDesignResponse(Design design) {
        DesignResponse dto = new DesignResponse();
        dto.setId(design.getId());
        dto.setUserId(design.getUser().getId());
        dto.setUserFullName(design.getUser().getFullName());
        dto.setProductId(design.getProduct().getId());
        dto.setProductName(design.getProduct().getName());
        dto.setDesignName(design.getDesignName());
        dto.setPreviewUrl(design.getPreviewUrl());
        dto.setDesignData(design.getDesignData());
        dto.setSize(design.getSize());
        dto.setColor(design.getColor());
        dto.setIsPublic(design.getIsPublic());
        dto.setLikeCount(design.getLikeCount());
        dto.setViewCount(design.getViewCount());
        dto.setCreatedAt(design.getCreatedAt());
        dto.setUpdatedAt(design.getUpdatedAt());
        return dto;
    }
}
