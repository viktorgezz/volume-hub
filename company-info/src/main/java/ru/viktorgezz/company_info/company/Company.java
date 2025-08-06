package ru.viktorgezz.company_info.company;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("moex_companies")
public class Company {

    @Id
    private Long id;
    private String name;
    private String ticker;
    private String figi;

}
