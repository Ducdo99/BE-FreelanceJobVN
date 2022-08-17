package group5.freelancejob.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageDto<T> {
    public List<T> data;
    public Integer pageNo;
    public Integer pageSize;
    public Integer totalPage;
    public Long totalCount;
    public Boolean hasPreviousPage;
    public Boolean hasNextPage;

    public PageDto(Page page, List<T> data, Integer pageNo, Integer pageSize) {
        this.data = data;
        this.totalPage = page.getTotalPages();
        this.totalCount = page.getTotalElements();
        this.hasNextPage = page.hasNext();
        this.hasPreviousPage = page.hasPrevious();
        this.pageNo = pageNo;
        this.pageSize = pageSize;
    }
}
