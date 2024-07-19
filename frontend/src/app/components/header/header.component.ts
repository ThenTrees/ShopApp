import { Component, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { NgbModule, NgbPopoverConfig } from '@ng-bootstrap/ng-bootstrap';
import { CommonModule } from '@angular/common';
import { UserResponse } from '../../responses/user/user.response';
import { UserService } from '../../service/user.service';
import { TokenService } from '../../service/token.service';
@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
  standalone: true,
  imports: [CommonModule, NgbModule, RouterModule],
})
export class HeaderComponent implements OnInit {
  userResponse?: UserResponse | null;
  isPopoverOpen = false;

  constructor(
    private userService: UserService,
    private tokenService: TokenService,
    private router: Router,
    private popoverConfig: NgbPopoverConfig
  ) {}
  ngOnInit(): void {
    this.userResponse = this.userService.getUserResponseFromLocalStorage();
  }

  togglePopover(event: Event) {
    event.preventDefault();
    this.isPopoverOpen = !this.isPopoverOpen;
  }

  handleItemClick(index: number) {
    //alert(`Clicked on "${index}"`);
    if (index === 0) {
      debugger;
      this.router.navigate(['/user-profile']);
    } else if (index === 2) {
      debugger;
      this.userService.removeUserFromLocalStorage();
      this.tokenService.removeToken();
      this.userResponse = this.userService.getUserResponseFromLocalStorage();
    }
    this.isPopoverOpen = false; // Close the popover after clicking an item
  }
}
