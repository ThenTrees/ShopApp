import { Router } from '@angular/router';
import { LoginDTO } from '../../dtos/user/login.dto';
import { UserService } from '../../service/user.service';
import { Component, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { LoginResponse } from '../../responses/user/Login.response';
import { Role } from '../../models/role.model';
import { UserResponse } from '../../responses/user/user.response';
import { TokenService } from '../../service/token.service';
import { RoleService } from '../../service/role.service';

import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, FooterComponent],
})
export class LoginComponent {
  @ViewChild('loginForm') loginForm!: NgForm;
  phoneNumber: string = '1231231231';
  password: string = '123456';

  roles: Role[] = []; // khai báo biến roles
  rememberMe: boolean = true;
  selectedRole: Role | undefined; // biến lưu giá trị được chọn tù dropdown
  userResponse?: UserResponse;
  showPassword: boolean = false;
  constructor(
    private router: Router,
    private userService: UserService,
    private tokenService: TokenService,
    private roleService: RoleService
  ) {}

  ngOnInit() {
    // gọi API danh sách role và gán vào biến roles
    debugger;
    this.roleService.getRoles().subscribe({
      next: (roles: Role[]) => {
        debugger;
        this.roles = roles;
        // chọn default là role dau tien từ api
        this.selectedRole = roles.length > 0 ? roles[1] : undefined; // admin's 0 and user's 1
      },
      complete: () => {
        debugger;
      },
      error: (err: any) => {
        debugger;
        console.error(`:::ERROR get Role:::`, err);
      },
    });
  }
  createAccount() {
    debugger;
    // Chuyển hướng người dùng đến trang đăng ký (hoặc trang tạo tài khoản)
    this.router.navigate(['/register']);
  }

  login() {
    debugger;
    const loginDto: LoginDTO = {
      phone_number: this.phoneNumber,
      password: this.password,
      role_id: this.selectedRole?.id ?? 2,
    };
    this.userService.login(loginDto).subscribe({
      next: (response: LoginResponse) => {
        const { token } = response;
        debugger;
        if (this.rememberMe) {
          this.tokenService.setToken(token);
          this.userService.getUserDetail(token).subscribe({
            next: (response1: any) => {
              this.userResponse = {
                ...response1.data,
                date_of_birth: new Date(response1.data.date_of_birth),
              };
              debugger;
              this.userService.saveUserResponseToLocalStorage(
                this.userResponse
              );
              this.router.navigate(['/']);
            },
            complete: () => {
              debugger;
            },
            error: (error: any) => {
              debugger;
              alert(`Cannot get user detail, error: ${error.error.message}`);
            },
          });
        }
      },
      complete: () => {
        debugger;
      },
      error: (error: any) => {
        debugger;
        alert(`Cannot login, error: ${error.error.message}`);
      },
    });
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }
}
