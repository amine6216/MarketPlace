package tn.esprit.marketplace.services;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.marketplace.entities.Role;
import tn.esprit.marketplace.entities.Store;
import tn.esprit.marketplace.entities.User;
import tn.esprit.marketplace.repositories.*;
import tn.esprit.marketplace.services.interfaces.IUserService;
import tn.esprit.marketplace.services.mapper.UserMapper;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements IUserService {
    private final UserRepository appUserRepository;


    @Autowired
    UserRepository userRepository;
    @Autowired
    AttachmentRepository attachementRepository;
    @Autowired
    FavoriteReopository favorisReopository;
    @Autowired
    BasketRepository basketRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    RoleRepository roleRepository;

    @Autowired
    StoreRepository storeRepository;
    @Autowired
    StoreService storeService;
    @Autowired
    UserMapper userMapper;

    @Override
    public User addUser(User user, String roleName) throws IllegalArgumentException {

        Role role = roleRepository.findByRoleName(roleName);
        if (role == null) {
            throw new IllegalArgumentException("Invalid role name: " + roleName);
        }

        user.setRole(role);
        user.setCreationDate(new Date());
        user.setPremium(false);

        if (roleName.equals("SELLER")) {
            Store store = new Store();
            store.setNameStore(user.getUserName() + "'s store");
            store.setCreationDate(user.getCreationDate());
            store.setUser(user);
            user.setStore(store);
            user.setBasketU(null);
            user.setFavoriteU(null);
            storeRepository.save(store);
        }

        return userRepository.save(user);
    }

    public User updateUser(long idUser, User user) throws UserNotFoundException {
        Optional<User> existingUser = userRepository.findById(idUser);
        if (existingUser.isPresent()) {
            User updatedUser = existingUser.get();
            updatedUser.setUserName(user.getUserName());
            updatedUser.setPassword(user.getPassword());
            updatedUser.setEmail(user.getEmail());
            updatedUser.setCreationDate(user.getCreationDate());
            updatedUser.setPhoneNumber(user.getPhoneNumber());
            updatedUser.setPremium(user.getPremium());
            updatedUser.setScore(user.getScore());
            updatedUser.setRole(user.getRole());
            return userRepository.save(updatedUser);
        } else {
            throw new UserNotFoundException("User not found with id: " + idUser);
        }
    }

    @Override
    public User deleteUser(long idUser) throws UserNotFoundException {
        Optional<User> userOptional = userRepository.findById(idUser);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException("User not found with id: " + idUser);
        }
        userRepository.deleteById(idUser);
        return null;
    }


   /* @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User appUser = appUserRepository.findByUsername(username);
        Assert.notNull(appUser, new UsernameNotFoundException(username).getMessage());
        return (UserDetails) new User(appUser.getUserName(), appUser.getPassword(), getAuthorities(appUser.getRole()));
    }

    private List<GrantedAuthority> getAuthorities(Role userRole) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(userRole.getRoleName()));
        return authorities;
    }*/

    /////////////////////////////////////////////////////////////////////////

    @Override
    public List<User> getAllUsers() {

        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long idUser) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(idUser);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UserNotFoundException("User not found with id: " + idUser);
        }
    }

    /*@Override
    public User addUserDTO(UserDTO userDTO) {
        if (userDTO.getRole().getRole().equals(RoleType.SELLER)) {
            Store store = storeService.addStore(userDTO.getStore());
          userDTO.setStore(store);
          }
        User user = userMapper.toEntity(userDTO);
        return userRepository.save(user);
    }*/
    @Override
    public void addUserWithStore(User user) {

        if (user.getRole().getRoleName().equals("SELLER")) {
            Store store = storeService.addStore(user.getStore());
            user.setStore(store);
        }
        userRepository.save(user);
    }

    @Override
    public User addUserWithoutStore(User user) {
        return userRepository.save(user);
    }

    @Override
    public void affectStoreToUser(Long idStore, Long idUser) {
        Store store = storeRepository.findById(idStore).get();
        User user = userRepository.findById(idUser).get();
        user.setStore(store);
        userRepository.save(user);
    }
}
