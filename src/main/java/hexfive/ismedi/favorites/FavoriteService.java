package hexfive.ismedi.favorites;

import hexfive.ismedi.domain.User;
import hexfive.ismedi.favorites.dto.ResFavoriteDto;
import hexfive.ismedi.global.exception.CustomException;
import hexfive.ismedi.global.exception.ErrorCode;
import hexfive.ismedi.medicine.Medicine;
import hexfive.ismedi.medicine.MedicineRepository;
import hexfive.ismedi.users.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static hexfive.ismedi.global.exception.ErrorCode.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;

    public void addFavorite(Long userId, Long medicineId) {
        boolean exists = favoriteRepository.existsByUserIdAndMedicineId(userId, medicineId);
        if (exists) {
            throw new IllegalStateException("이미 즐겨찾기된 약입니다.");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new IllegalArgumentException("해당 약을 찾을 수 없습니다"));

        Favorite favorite = Favorite.builder()
                .user(user)
                .medicine(medicine)
                .build();

        favoriteRepository.save(favorite);
    }

    public List<ResFavoriteDto> getFavoriteById(Long userId){
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);
        if (favorites.isEmpty()) {
            return Collections.emptyList();
        }
        return favorites.stream().map(ResFavoriteDto::fromEntity).collect(Collectors.toList());
    }

    @Transactional
    public void removeFavorite(Long userId, Long medicineId){
        Favorite favorite = favoriteRepository.findByUserIdAndMedicineId(userId, medicineId)
                .orElseThrow(() -> new IllegalArgumentException("즐겨찾기 내역이 없습니다."));
        favoriteRepository.delete(favorite);
    }
}
