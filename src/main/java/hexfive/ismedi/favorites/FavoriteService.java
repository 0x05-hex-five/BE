package hexfive.ismedi.favorites;

import hexfive.ismedi.domain.User;
import hexfive.ismedi.favorites.dto.ResFavoriteDto;
import hexfive.ismedi.global.exception.CustomException;
import hexfive.ismedi.medicine.Medicine;
import hexfive.ismedi.medicine.MedicineRepository;
import hexfive.ismedi.users.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static hexfive.ismedi.global.exception.ErrorCode.FAVORITE_ALREADY_EXISTS;
import static hexfive.ismedi.global.exception.ErrorCode.USER_NOT_FOUND;
import static hexfive.ismedi.global.exception.ErrorCode.MEDICINE_NOT_FOUND;
import static hexfive.ismedi.global.exception.ErrorCode.FAVORITE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final MedicineRepository medicineRepository;

    public void addFavorite(Long userId, Long medicineId) {
        boolean exists = favoriteRepository.existsByUserIdAndMedicineId(userId, medicineId);
        if (exists) {
            throw new CustomException(FAVORITE_ALREADY_EXISTS);
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));

        Medicine medicine = medicineRepository.findById(medicineId)
                .orElseThrow(() -> new CustomException(MEDICINE_NOT_FOUND));

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
                .orElseThrow(() -> new CustomException(FAVORITE_NOT_FOUND));
        favoriteRepository.delete(favorite);
    }
}
