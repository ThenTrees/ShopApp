import { Router } from '@angular/router';
import { LoginDTO } from '../../dtos/user/login.dto';
import { UserService } from '../../service/user.service';
import { Component, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { LoginResponse } from '../../responses/user/Login.response';
import { TokenService } from 'src/app/service/token.service';
import { RoleService } from 'src/app/service/role.service';
import { Role } from 'src/app/models/role.model';
import { UserResponse } from 'src/app/responses/user/user.response';
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  @ViewChild('loginForm') loginForm!: NgForm;
  phoneNumber: string = '';
  password: string = '';
  roles: Role[] = []; // khai báo biến roles
  rememberMe: boolean = false;
  selectedRole: Role | undefined; // biến lưu giá trị được chọn tù dropdown
  userResponse?: UserResponse;
  constructor(
    private router: Router,
    private userService: UserService,
    private tokenService: TokenService,
    private roleService: RoleService
  ) {
    this.phoneNumber = '';
    this.password = '';
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
        debugger;
        const { token } = response;
        if (this.rememberMe) {
          this.tokenService.setToken(token);
          // lấy chi tiết thông tin user thông qua token
          this.userService.saveInfoToLocalStorage(token).subscribe({
            next: (response: any) => {
              debugger;
            },
            complete: () => {
              debugger;
            },
            error: (error: Error) => {
              debugger;
              alert(`Error saving token to local storage: ${error.message}`);
            },
          });
        }
        this.router.navigate(['/']);
      },
      complete: () => {
        debugger;
      },
      error: (error) => {
        debugger;
        alert(`Cannot login, error: ${error.error}`);
      },
    });
  }

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
}
