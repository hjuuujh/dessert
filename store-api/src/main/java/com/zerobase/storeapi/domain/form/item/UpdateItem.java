package com.zerobase.storeapi.domain.form.item;

import com.zerobase.storeapi.domain.form.option.CreateOption;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class UpdateItem {
    private Long id;
    private Long storeId;
    private String name;
    private String thumbnailUrl;
    private String description;
    private String descriptionUrl;
    private List<CreateOption> options;
}
