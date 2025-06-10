package com.nhnacademy.bookapi.book.controller.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCreateRequest {

    @NotBlank
    @Size(min = 1, max = 255)
    private String title;

    private String description;

    private String toc;

    @NotBlank
    @Size(min = 1, max = 255)
    private String publisher;

    @NotBlank
    @Size(min = 1, max = 255)
    private String author;

    @NotNull
    private LocalDate publishedDate;

    @NotNull
    @Pattern(regexp = "^.{13}$")
    private String isbn;

    @Min(value = 1)
    private Integer originalPrice;

    @Min(value = 1)
    private Integer salePrice;

    @NotNull
    private Boolean wrappable;

    @Min(value = 0)
    private Integer stock;

}
