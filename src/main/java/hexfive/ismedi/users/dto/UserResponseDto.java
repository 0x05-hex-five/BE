package hexfive.ismedi.users.dto;

import hexfive.ismedi.domain.User;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Builder
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String name;
    private String email;
    private LocalDate birth;
    private String gender;
    private Boolean pregnant;
    private Boolean alert;

    public static UserResponseDto fromEntity(User user){
        return UserResponseDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .birth(user.getBirth())
                .gender(String.valueOf(user.getGender()))
                .pregnant(user.getPregnant())
                .alert(user.getAlert())
                .build();
    }
}
