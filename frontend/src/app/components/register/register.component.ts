import { UserService } from '../../service/user.service';
import { Component, ViewChild } from '@angular/core';
import { NgForm, FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RegisterDto } from '../../dtos/user/register.dto';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from '../header/header.component';
import { FooterComponent } from '../footer/footer.component';
@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule, HeaderComponent, FooterComponent],
})
export class RegisterComponent {
  @ViewChild('registerForm') registerForm!: NgForm;
  phoneNumber: string;
  password: string;
  retypePassword: string;
  fullName: string;
  address: string;
  dateOfBirth: Date;
  isAccepted: boolean;
  showPassword: boolean = false;
  showRetypePassword: boolean = false;

  constructor(private router: Router, private UserService: UserService) {
    this.phoneNumber = '';
    this.password = '';
    this.retypePassword = '';
    this.fullName = '';
    this.address = '';
    this.dateOfBirth = new Date();
    this.dateOfBirth.setFullYear(this.dateOfBirth.getFullYear() - 18);
    this.isAccepted = true;
  }

  onPhoneNumberChange() {
    console.log(`Phone number changed to: ${this.phoneNumber}`);
  }

  register() {
    const registerDto: RegisterDto = {
      fullname: this.fullName,
      phone_number: this.phoneNumber,
      address: this.address,
      password: this.password,
      retype_password: this.retypePassword,
      date_of_birth: this.dateOfBirth,
      is_accepted: this.isAccepted,
      facebook_account_id: 0,
      google_account_id: 0,
      role_id: 2,
    };

    debugger;

    this.UserService.register(registerDto).subscribe({
      next: (response: any) => {
        debugger;
        this.router.navigate(['/login']);
      },
      complete: () => {
        debugger;
      },
      error: (error) => {
        alert(`Cannot register, error: ${error.error}`);
      },
    });
  }

  checkPasswordMatch() {
    if (this.password !== this.retypePassword) {
      console.log(this.password, this.retypePassword);

      this.registerForm.form.controls['retypePassword'].setErrors({
        passwordMismatch: true,
      });
    } else {
      this.registerForm.form.controls['retypePassword'].setErrors(null);
    }
  }

  checkAge() {
    if (this.dateOfBirth) {
      let birthday = new Date(this.dateOfBirth);
      let today = new Date();
      let age = today.getFullYear() - birthday.getFullYear();
      let monthDiff = today.getMonth() - birthday.getMonth();
      if (
        monthDiff < 0 ||
        (monthDiff === 0 && today.getDate() < birthday.getDate())
      ) {
        age--;
      }

      if (age < 18) {
        this.registerForm.form.controls['dateOfBirth'].setErrors({
          invalidAge: true,
        });
      } else {
        this.registerForm.form.controls['dateOfBirth'].setErrors(null);
      }
    }
  }

  toggleShowPassword() {
    this.showPassword = !this.showPassword;
  }

  toggleShowRetypePassword() {
    this.showRetypePassword = !this.showRetypePassword;
  }
}
