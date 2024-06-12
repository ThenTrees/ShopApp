import { IsNotEmpty, IsPhoneNumber } from 'class-validator';

export class LoginDTO {
  @IsNotEmpty()
  @IsPhoneNumber()
  phone_number: string;

  @IsNotEmpty()
  password: string;

  role_id: number;

  constructor(data: any) {
    this.phone_number = data.phone_number;
    this.password = data.password;
    this.role_id = data.role_id;
  }
}
