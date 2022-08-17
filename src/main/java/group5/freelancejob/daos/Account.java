package group5.freelancejob.daos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "Account")
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "pwd")
    private String pwd;
    @Column(name = "phone")
    private String phone;
    @Column(name = "email")
    private String email;
    @Column(name = "deposit")
    private float deposit;
    @Column(name = "avatar")
    private String avatar;

    @OneToOne(mappedBy = "account")
    private Freelancer freelancer;
    @OneToOne(mappedBy = "account")
    private Recruiter recruiter;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "account")
    private List<PaymentHistory> paymentHistories;

    @OneToMany(mappedBy = "fromAccount")
    private List<Message> fromMessages;
    @OneToMany(mappedBy = "toAccount")
    private List<Message> toMessages;
}
